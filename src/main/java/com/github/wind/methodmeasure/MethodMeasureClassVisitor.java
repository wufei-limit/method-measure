package com.github.wind.methodmeasure;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by jiaokang on 2021/11/6
 */
public class MethodMeasureClassVisitor extends ClassVisitor implements Opcodes{

    private String mClassName;
    private boolean isIgnoreClass = false;

    public MethodMeasureClassVisitor(ClassVisitor visitor) {
        super(Opcodes.ASM7,visitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.mClassName = name;
        if (!isIgnoreClass && interfaces!=null && interfaces.length > 0){
            for (String inter :interfaces){
                if (inter.contains("ViewBinding")) {
                    isIgnoreClass = true;
                    break;
                }
            }
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (isIgnoreClass) return mv;
        if (name.contains("<init>")) return mv;
        return new MethodMeasureMethodVisitor(mClassName,Opcodes.ASM7,mv,access,name,desc);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
