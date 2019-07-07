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

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.Value;

/**
 * A {@link Value} that is represented with its type in a seven types type system. This type system distinguishes the UNINITIALZED, INT, FLOAT, LONG, DOUBLE, REFERENCE and RETURNADDRESS types.
 *
 * @author Eric Bruneton
 */
public class CoffeeValue implements Value {

	/** An uninitialized value. */
	public static final CoffeeValue UNINITIALIZED_VALUE = new CoffeeValue(null);

	/** A byte, boolean, char, short, or int value. */
	public static final CoffeeValue INT_VALUE = new CoffeeValue(Type.INT_TYPE);

	/** A float value. */
	public static final CoffeeValue FLOAT_VALUE = new CoffeeValue(Type.FLOAT_TYPE);

	/** A long value. */
	public static final CoffeeValue LONG_VALUE = new CoffeeValue(Type.LONG_TYPE);

	/** A double value. */
	public static final CoffeeValue DOUBLE_VALUE = new CoffeeValue(Type.DOUBLE_TYPE);

	/** An object or array reference value. */
	public static final CoffeeValue REFERENCE_VALUE = new CoffeeValue(Type.getObjectType("java/lang/Object"));

	/** A return address value (produced by a jsr instruction). */
	public static final CoffeeValue RETURNADDRESS_VALUE = new CoffeeValue(Type.VOID_TYPE);

	/** The {@link Type} of this value, or {@literal null} for uninitialized values. */
	private final Type type;

	private CoffeeValue merge1;
	private CoffeeValue merge2;

	/**
	 * Constructs a new {@link CoffeeValue} of the given type.
	 *
	 * @param type
	 *          the value type.
	 */
	public CoffeeValue(final Type type) {
		this.type = type;
	}

	/**
	 * Returns the {@link Type} of this value.
	 *
	 * @return the {@link Type} of this value.
	 */
	public Type getType() {
		return type;
	}

	@Override
	public int getSize() {
		return type == Type.LONG_TYPE || type == Type.DOUBLE_TYPE ? 2 : 1;
	}

	public CoffeeValue(CoffeeValue merge1, CoffeeValue merge2) {
		type = null;
		this.merge1 = merge1;
		this.merge2 = merge2;
	}

	public boolean isMerged() {
		return merge1 != null && merge2 != null;
	}

	/**
	 * Returns whether this value corresponds to an object or array reference.
	 *
	 * @return whether this value corresponds to an object or array reference.
	 */
	public boolean isReference() {
		return type != null && (type.getSort() == Type.OBJECT || type.getSort() == Type.ARRAY) && !isMerged();
	}

	@Override
	public boolean equals(final Object value) {
		if (value == this) {
			return true;
		} else if (value instanceof CoffeeValue) {
			if (isMerged()) {
				return merge1.equals(((CoffeeValue) value).merge1) && merge2.equals(((CoffeeValue) value).merge2);
			}
			if (type == null) {
				return ((CoffeeValue) value).type == null;
			} else {
				return type.equals(((CoffeeValue) value).type);
			}
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return type == null ? 0 : type.hashCode();
	}

	@Override
	public String toString() {
		if (isMerged()) {
			return "M(" + merge1.toString() + "," + merge2.toString() + ")";
		}
		if (this == UNINITIALIZED_VALUE) {
			return ".";
		} else if (this == RETURNADDRESS_VALUE) {
			return "A";
		} else if (this == REFERENCE_VALUE) {
			return "R";
		} else {
			return type.getDescriptor();
		}
	}
}
