package org.oopscraft.apps.batch.dependency;


import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.springframework.asm.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class DependencyVisitor extends ClassVisitor {

    protected static final int OPCODE = Opcodes.ASM9;

    Set<String> packages = new HashSet<String>();

    Map<String, Map<String, Integer>> groups = new HashMap<String, Map<String, Integer>>();

    Map<String, Integer> current;

    Set<String> classes = new HashSet<String>();

    public DependencyVisitor() {
        super(OPCODE);
    }

    /**
     * getGlobals
     *
     * @return
     */
    public Map<String, Map<String, Integer>> getGlobals() {
        return groups;
    }

    public Set<String> getPackages() {
        return packages;
    }

    public Set<String> getClasses() {
        return classes;
    }

    /**
     * Override ClassVisiter method
     */
    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        log.trace("DependencyVisitor.visit[{}]", name);
        String p = getGroupKey(name);
        current = groups.get(p);
        if (current == null) {
            current = new HashMap<String, Integer>();
            groups.put(p, current);
        }

        if (signature == null) {
            if (superName != null) {
                addInternalName(superName);
            }
            addInternalNames(interfaces);
        } else {
            addSignature(signature);
        }
    }

    /**
     * visitAnnotation
     */
    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        addDesc(desc);
        return new AnnotationDependencyVisitor();
    }

    /**
     * visitField
     */
    @Override
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        if (signature == null) {
            addDesc(desc);
        } else {
            addTypeSignature(signature);
        }
        if (value instanceof Type) {
            addType((Type) value);
        }
        return new FieldDependencyVisitor();
    }

    /**
     * visitMethod
     */
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        if (signature == null) {
            addMethodDesc(desc);
        } else {
            addSignature(signature);
        }
        addInternalNames(exceptions);
        return new MethodDependencyVisitor();
    }

    private String getGroupKey(String name) {
        int n = name.lastIndexOf('/');
        if (n > -1) {
            name = name.substring(0, n);
        }
        packages.add(name);
        return name;
    }

    private void addName(final String name) {
        if (name == null) {
            return;
        }

        // adds dependencies
        classes.add(name);

        String p = getGroupKey(name);
        if (current.containsKey(p)) {
            current.put(p, current.get(p) + 1);
        } else {
            current.put(p, 1);
        }
    }

    void addInternalName(final String name) {
        addType(Type.getObjectType(name));
    }

    private void addInternalNames(final String[] names) {
        for (int i = 0; names != null && i < names.length; i++) {
            addInternalName(names[i]);
        }
    }

    void addDesc(final String desc) {
        addType(Type.getType(desc));
    }

    void addMethodDesc(final String desc) {
        addType(Type.getReturnType(desc));
        Type[] types = Type.getArgumentTypes(desc);
        for (int i = 0; i < types.length; i++) {
            addType(types[i]);
        }
    }

    void addType(final Type t) {
        switch (t.getSort()) {
            case Type.ARRAY:
                addType(t.getElementType());
                break;
            case Type.OBJECT:
                addName(t.getInternalName());
                break;
            case Type.METHOD:
                addMethodDesc(t.getDescriptor());
                break;
        }
    }

    private void addSignature(final String signature) {
        if (signature != null) {
            new SignatureReader(signature).accept(new SignatureDependencyVisitor());
        }
    }

    void addTypeSignature(final String signature) {
        if (signature != null) {
            new SignatureReader(signature).acceptType(new SignatureDependencyVisitor());
        }
    }

    void addConstant(final Object cst) {
        if (cst instanceof Type) {
            addType((Type) cst);
        } else if (cst instanceof Handle) {
            Handle h = (Handle) cst;
            addInternalName(h.getOwner());
            addMethodDesc(h.getDesc());
        }
    }

    /**
     * AnnotationDependencyVisitor
     */
    class AnnotationDependencyVisitor extends AnnotationVisitor {

        public AnnotationDependencyVisitor() {
            super(OPCODE);
        }

        @Override
        public void visit(final String name, final Object value) {
            if (value instanceof Type) {
                addType((Type) value);
            }
        }

        @Override
        public void visitEnum(final String name, final String desc, final String value) {
            addDesc(desc);
        }

        @Override
        public AnnotationVisitor visitAnnotation(final String name, final String desc) {
            addDesc(desc);
            return this;
        }

        @Override
        public AnnotationVisitor visitArray(final String name) {
            return this;
        }
    }

    /**
     * FieldDependencyVisitor
     */
    class FieldDependencyVisitor extends FieldVisitor {

        public FieldDependencyVisitor() {
            super(OPCODE);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            addDesc(desc);
            return new AnnotationDependencyVisitor();
        }
    }

    /**
     * MethodDependencyVisitor
     */
    class MethodDependencyVisitor extends MethodVisitor {

        public MethodDependencyVisitor() {
            super(OPCODE);
        }

        @Override
        public AnnotationVisitor visitAnnotationDefault() {
            return new AnnotationDependencyVisitor();
        }

        @Override
        public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
            addDesc(desc);
            return new AnnotationDependencyVisitor();
        }

        @Override
        public AnnotationVisitor visitParameterAnnotation(final int parameter, final String desc,
                                                          final boolean visible) {
            addDesc(desc);
            return new AnnotationDependencyVisitor();
        }

        @Override
        public void visitTypeInsn(final int opcode, final String type) {
            addType(Type.getObjectType(type));
        }

        @Override
        public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
            addInternalName(owner);
            addDesc(desc);
        }

        @Override
        public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
            addInternalName(owner);
            addMethodDesc(desc);
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
            addMethodDesc(desc);
            addConstant(bsm);
            for (int i = 0; i < bsmArgs.length; i++) {
                addConstant(bsmArgs[i]);
            }
        }

        @Override
        public void visitLdcInsn(final Object cst) {
            addConstant(cst);
        }

        @Override
        public void visitMultiANewArrayInsn(final String desc, final int dims) {
            addDesc(desc);
        }

        @Override
        public void visitLocalVariable(final String name, final String desc, final String signature, final Label start,
                                       final Label end, final int index) {
            addTypeSignature(signature);
        }

        @Override
        public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type) {
            if (type != null) {
                addInternalName(type);
            }
        }
    }

    /**
     * SignatureDependencyVisitor
     */
    class SignatureDependencyVisitor extends SignatureVisitor {

        String signatureClassName;

        public SignatureDependencyVisitor() {
            super(OPCODE);
        }

        @Override
        public void visitClassType(final String name) {
            signatureClassName = name;
            addInternalName(name);
        }

        @Override
        public void visitInnerClassType(final String name) {
            signatureClassName = signatureClassName + "$" + name;
            addInternalName(signatureClassName);
        }
    }
}