// ASM: a very small and fast Java bytecode manipulation framework
// Copyright (c) 2000-2011 INRIA, France Telecom
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
// 3. Neither the name of the copyright holders nor the names of its
//    contributors may be used to endorse or promote products derived from
//    this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
// THE POSSIBILITY OF SUCH DAMAGE.
package me.nov.cafebabe.utils.analysis;

import java.util.List;

import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Interpreter;

/**
 * An {@link Interpreter} for {@link BasicValue} values.
 *
 * @author Eric Bruneton
 * @author Bing Ran
 */
public class CoffeeInterpreter extends Interpreter<CoffeeValue> implements Opcodes {

	/**
	 * Special type used for the {@literal null} literal. This is an object reference type with descriptor 'Lnull;'.
	 */
	public static final Type NULL_TYPE = Type.getObjectType("null");

	/**
	 * Constructs a new {@link CoffeeInterpreter} for the latest ASM API version. <i>Subclasses must not use this constructor</i>. Instead, they must use the {@link #BasicInterpreter(int)} version.
	 */
	public CoffeeInterpreter() {
		super(ASM7);
		if (getClass() != CoffeeInterpreter.class) {
			throw new IllegalStateException();
		}
	}

	/**
	 * Constructs a new {@link CoffeeInterpreter}.
	 *
	 * @param api
	 *          the ASM API version supported by this interpreter. Must be one of {@link org.objectweb.asm.Opcodes#ASM4}, {@link org.objectweb.asm.Opcodes#ASM5}, {@link org.objectweb.asm.Opcodes#ASM6} or {@link org.objectweb.asm.Opcodes#ASM7}.
	 */
	protected CoffeeInterpreter(final int api) {
		super(api);
	}

	@Override
	public CoffeeValue newValue(final Type type) {
		if (type == null) {
			return CoffeeValue.UNINITIALIZED_VALUE;
		}
		switch (type.getSort()) {
		case Type.VOID:
			return null;
		case Type.BOOLEAN:
		case Type.CHAR:
		case Type.BYTE:
		case Type.SHORT:
		case Type.INT:
			return CoffeeValue.INT_VALUE;
		case Type.FLOAT:
			return CoffeeValue.FLOAT_VALUE;
		case Type.LONG:
			return CoffeeValue.LONG_VALUE;
		case Type.DOUBLE:
			return CoffeeValue.DOUBLE_VALUE;
		case Type.ARRAY:
		case Type.OBJECT:
			return new CoffeeValue(type);
		default:
			throw new AssertionError();
		}
	}

	@Override
	public CoffeeValue newOperation(final AbstractInsnNode insn) throws AnalyzerException {
		switch (insn.getOpcode()) {
		case ACONST_NULL:
			return newValue(NULL_TYPE);
		case ICONST_M1:
		case ICONST_0:
		case ICONST_1:
		case ICONST_2:
		case ICONST_3:
		case ICONST_4:
		case ICONST_5:
			return CoffeeValue.INT_VALUE;
		case LCONST_0:
		case LCONST_1:
			return CoffeeValue.LONG_VALUE;
		case FCONST_0:
		case FCONST_1:
		case FCONST_2:
			return CoffeeValue.FLOAT_VALUE;
		case DCONST_0:
		case DCONST_1:
			return CoffeeValue.DOUBLE_VALUE;
		case BIPUSH:
		case SIPUSH:
			return CoffeeValue.INT_VALUE;
		case LDC:
			Object value = ((LdcInsnNode) insn).cst;
			if (value instanceof Integer) {
				return CoffeeValue.INT_VALUE;
			} else if (value instanceof Float) {
				return CoffeeValue.FLOAT_VALUE;
			} else if (value instanceof Long) {
				return CoffeeValue.LONG_VALUE;
			} else if (value instanceof Double) {
				return CoffeeValue.DOUBLE_VALUE;
			} else if (value instanceof String) {
				return newValue(Type.getObjectType("java/lang/String"));
			} else if (value instanceof Type) {
				int sort = ((Type) value).getSort();
				if (sort == Type.OBJECT || sort == Type.ARRAY) {
					return newValue(Type.getObjectType("java/lang/Class"));
				} else if (sort == Type.METHOD) {
					return newValue(Type.getObjectType("java/lang/invoke/MethodType"));
				} else {
					throw new AnalyzerException(insn, "Illegal LDC value " + value);
				}
			} else if (value instanceof Handle) {
				return newValue(Type.getObjectType("java/lang/invoke/MethodHandle"));
			} else if (value instanceof ConstantDynamic) {
				return newValue(Type.getType(((ConstantDynamic) value).getDescriptor()));
			} else {
				throw new AnalyzerException(insn, "Illegal LDC value " + value);
			}
		case JSR:
			return CoffeeValue.RETURNADDRESS_VALUE; // missing implementation
		case GETSTATIC:
			return newValue(Type.getType(((FieldInsnNode) insn).desc));
		case NEW:
			return newValue(Type.getObjectType(((TypeInsnNode) insn).desc));
		default:
			throw new AssertionError();
		}
	}

	@Override
	public CoffeeValue copyOperation(final AbstractInsnNode insn, final CoffeeValue value) throws AnalyzerException {
		return value;
	}

	@Override
	public CoffeeValue unaryOperation(final AbstractInsnNode insn, final CoffeeValue value) throws AnalyzerException {
		switch (insn.getOpcode()) {
		case INEG:
		case IINC:
		case L2I:
		case F2I:
		case D2I:
		case I2B:
		case I2C:
		case I2S:
			return CoffeeValue.INT_VALUE;
		case FNEG:
		case I2F:
		case L2F:
		case D2F:
			return CoffeeValue.FLOAT_VALUE;
		case LNEG:
		case I2L:
		case F2L:
		case D2L:
			return CoffeeValue.LONG_VALUE;
		case DNEG:
		case I2D:
		case L2D:
		case F2D:
			return CoffeeValue.DOUBLE_VALUE;
		case IFEQ:
		case IFNE:
		case IFLT:
		case IFGE:
		case IFGT:
		case IFLE:
		case TABLESWITCH:
		case LOOKUPSWITCH:
		case IRETURN:
		case LRETURN:
		case FRETURN:
		case DRETURN:
		case ARETURN:
		case PUTSTATIC:
			return null;
		case GETFIELD:
			return newValue(Type.getType(((FieldInsnNode) insn).desc));
		case NEWARRAY:
			switch (((IntInsnNode) insn).operand) {
			case T_BOOLEAN:
				return newValue(Type.getType("[Z"));
			case T_CHAR:
				return newValue(Type.getType("[C"));
			case T_BYTE:
				return newValue(Type.getType("[B"));
			case T_SHORT:
				return newValue(Type.getType("[S"));
			case T_INT:
				return newValue(Type.getType("[I"));
			case T_FLOAT:
				return newValue(Type.getType("[F"));
			case T_DOUBLE:
				return newValue(Type.getType("[D"));
			case T_LONG:
				return newValue(Type.getType("[J"));
			default:
				break;
			}
			throw new AnalyzerException(insn, "Invalid array type");
		case ANEWARRAY:
			return newValue(Type.getType("[" + Type.getObjectType(((TypeInsnNode) insn).desc)));
		case ARRAYLENGTH:
			return CoffeeValue.INT_VALUE;
		case ATHROW:
			return null;
		case CHECKCAST:
			return newValue(Type.getObjectType(((TypeInsnNode) insn).desc));
		case INSTANCEOF:
			return CoffeeValue.INT_VALUE;
		case MONITORENTER:
		case MONITOREXIT:
		case IFNULL:
		case IFNONNULL:
			return null;
		default:
			throw new AssertionError();
		}
	}

	@Override
	public CoffeeValue binaryOperation(final AbstractInsnNode insn, final CoffeeValue value1, final CoffeeValue value2)
			throws AnalyzerException {
		switch (insn.getOpcode()) {
		case IALOAD:
		case BALOAD:
		case CALOAD:
		case SALOAD:
		case IADD:
		case ISUB:
		case IMUL:
		case IDIV:
		case IREM:
		case ISHL:
		case ISHR:
		case IUSHR:
		case IAND:
		case IOR:
		case IXOR:
			return CoffeeValue.INT_VALUE;
		case FALOAD:
		case FADD:
		case FSUB:
		case FMUL:
		case FDIV:
		case FREM:
			return CoffeeValue.FLOAT_VALUE;
		case LALOAD:
		case LADD:
		case LSUB:
		case LMUL:
		case LDIV:
		case LREM:
		case LSHL:
		case LSHR:
		case LUSHR:
		case LAND:
		case LOR:
		case LXOR:
			return CoffeeValue.LONG_VALUE;
		case DALOAD:
		case DADD:
		case DSUB:
		case DMUL:
		case DDIV:
		case DREM:
			return CoffeeValue.DOUBLE_VALUE;
		case AALOAD:
			return CoffeeValue.REFERENCE_VALUE;
		case LCMP:
		case FCMPL:
		case FCMPG:
		case DCMPL:
		case DCMPG:
			return CoffeeValue.INT_VALUE;
		case IF_ICMPEQ:
		case IF_ICMPNE:
		case IF_ICMPLT:
		case IF_ICMPGE:
		case IF_ICMPGT:
		case IF_ICMPLE:
		case IF_ACMPEQ:
		case IF_ACMPNE:
		case PUTFIELD:
			return null;
		default:
			throw new AssertionError();
		}
	}

	@Override
	public CoffeeValue ternaryOperation(final AbstractInsnNode insn, final CoffeeValue value1, final CoffeeValue value2,
			final CoffeeValue value3) throws AnalyzerException {
		return null;
	}

	@Override
	public CoffeeValue naryOperation(final AbstractInsnNode insn, final List<? extends CoffeeValue> values)
			throws AnalyzerException {
		int opcode = insn.getOpcode();
		if (opcode == MULTIANEWARRAY) {
			return newValue(Type.getType(((MultiANewArrayInsnNode) insn).desc));
		} else if (opcode == INVOKEDYNAMIC) {
			return newValue(Type.getReturnType(((InvokeDynamicInsnNode) insn).desc));
		} else {
			return newValue(Type.getReturnType(((MethodInsnNode) insn).desc));
		}
	}

	@Override
	public void returnOperation(final AbstractInsnNode insn, final CoffeeValue value, final CoffeeValue expected)
			throws AnalyzerException {
		// Nothing to do.
	}

	@Override
	public CoffeeValue merge(final CoffeeValue value1, final CoffeeValue value2) {
		if (value1 == CoffeeValue.UNINITIALIZED_VALUE) {
			return CoffeeValue.UNINITIALIZED_VALUE;
		}
		if (value2 == CoffeeValue.UNINITIALIZED_VALUE) {
			return CoffeeValue.UNINITIALIZED_VALUE;
		}
		if (NULL_TYPE.equals(value1.getType())) {
			return value2;
		}
		if (NULL_TYPE.equals(value2.getType())) {
			return value1;
		}
		if (!value1.equals(value2)) {
			return new CoffeeValue(value1, value2);
		}
		return value1;
	}
}
