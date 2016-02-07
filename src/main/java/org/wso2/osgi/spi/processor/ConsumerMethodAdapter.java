package org.wso2.osgi.spi.processor;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class ConsumerMethodAdapter extends GeneratorAdapter implements Opcodes {
    private final Type DYNAMIC_INJECT_TYPE = Type.getType(DynamicInject.class);
    private final Type CLASS_TYPE = Type.getType(Class.class);
    private final Type STRING_TYPE = Type.getType(String.class);
    private final Type CLASSLOADER_TYPE = Type.getType(ClassLoader.class);
    private final ConsumerClassVisitor consumerClassVisitor;
    private Type lastLdcType;
    private boolean isLdcTracked = false;

    public ConsumerMethodAdapter(ConsumerClassVisitor cv, MethodVisitor mv, int access, String name, String desc) {
        super(ASM5, mv, access, name, desc);
        this.consumerClassVisitor = cv;
    }


    @Override
    public void visitLdcInsn(Object cst) {
        if (!isLdcTracked && cst instanceof Type) {
            lastLdcType = ((Type) cst);
        }
        super.visitLdcInsn(cst);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {

        if (opcode == INVOKESTATIC && name.equals("load")) {

            isLdcTracked = true;

            Label startTry = newLabel();
            Label endTry = newLabel();

            //start try block
            visitTryCatchBlock(startTry, endTry, endTry, null);
            mark(startTry);

            // Add: DynamicInject.storeContextClassloader();
            invokeStatic(DYNAMIC_INJECT_TYPE, new Method("storeContextClassloader", Type.VOID_TYPE, new Type[0]));


            //Load the strings, method parameter and target
            visitLdcInsn(lastLdcType);
            visitLdcInsn(consumerClassVisitor.getConsumerClassType());

            //Change the class on the stack into a classloader
            invokeVirtual(CLASS_TYPE, new Method("getClassLoader",
                    CLASSLOADER_TYPE, new Type[0]));

            //Call our util method
            invokeStatic(DYNAMIC_INJECT_TYPE, new Method("fixContextClassloader", Type.VOID_TYPE,
                    new Type[]{ CLASS_TYPE, CLASSLOADER_TYPE}));

            //Call the original instruction
            super.visitMethodInsn(opcode, owner, name, desc, itf);

            //If no exception then go to the finally (finally blocks are a catch block with a jump)
            Label afterCatch = newLabel();
            goTo(afterCatch);


            //start the catch
            mark(endTry);
            //Run the restore method then throw on the exception
            invokeStatic(DYNAMIC_INJECT_TYPE, new Method("restoreContextClassloader", Type.VOID_TYPE, new Type[0]));
            throwException();

            //start the finally
            mark(afterCatch);
            //Run the restore and continue
            invokeStatic(DYNAMIC_INJECT_TYPE, new Method("restoreContextClassloader", Type.VOID_TYPE, new Type[0]));
            consumerClassVisitor.setModified();
        } else {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }
}
