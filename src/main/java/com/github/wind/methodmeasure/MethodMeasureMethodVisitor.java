package com.github.wind.methodmeasure;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Created by jiaokang on 2021/11/8
 */
public class MethodMeasureMethodVisitor extends AdviceAdapter {
    private final String className;
    private int index = 0;
    protected MethodMeasureMethodVisitor(String className, int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor);
        this.className = className;
    }

    @Override
    public void visitParameter(String name, int access) {
        System.out.println(getName() +" : param :"+name);
        index++;
        super.visitParameter(name, access);
    }

    @Override
    protected void onMethodEnter() {
        // 调用类型,调用对象,方法名称,方法描述((参数列表)返回值)，是否接口
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()"+Type.LONG_TYPE.toString(), false);
        index = newLocal(Type.LONG_TYPE);
        //记录方法进入时间
        mv.visitVarInsn(Opcodes.LSTORE, index);
    }

    @Override
    protected void onMethodExit(int opcode) {
        // 调用类型,调用对象,方法名称,方法描述((参数列表)返回值)，是否接口
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()"+Type.LONG_TYPE.toString(), false);
        int endIndex = newLocal(Type.LONG_TYPE);
        mv.visitVarInsn(LSTORE,endIndex);
        mv.visitVarInsn(LLOAD,endIndex);
        mv.visitVarInsn(LLOAD,index);
        mv.visitInsn(LSUB);
        mv.visitVarInsn(LSTORE,endIndex);
        mv.visitLdcInsn(this.className);
        mv.visitLdcInsn(this.getName());
        mv.visitVarInsn(LLOAD,endIndex);
        mv.visitMethodInsn(INVOKESTATIC,  MethodMeasureConfig.getLogName(), MethodMeasureConfig.getMethodName(), "(Ljava/lang/String;Ljava/lang/String;J)V", false);
    }
}
