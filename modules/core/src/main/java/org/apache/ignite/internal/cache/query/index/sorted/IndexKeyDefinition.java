/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.cache.query.index.sorted;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.apache.ignite.internal.cache.query.index.Order;
import org.apache.ignite.internal.cache.query.index.SortOrder;
import org.apache.ignite.internal.cache.query.index.sorted.keys.IndexKey;
import org.apache.ignite.internal.cache.query.index.sorted.keys.NullIndexKey;
import org.apache.ignite.internal.util.typedef.internal.U;

/**
 * Defines a signle index key.
 */
public class IndexKeyDefinition implements Externalizable {
    /** */
    private static final long serialVersionUID = 0L;

    /** Index key type. {@link IndexKeyTypes}. */
    private int idxType;

    /** Order. */
    private Order order;

    /** Precision for variable length key types. */
    private int precision;

    /** */
    public IndexKeyDefinition() {
        // No-op.
    }

    /** */
    public IndexKeyDefinition(int idxType, Order order, long precision) {
        this.idxType = idxType;
        this.order = order;

        // Workaround due to wrong type conversion (int -> long).
        if (precision >= Integer.MAX_VALUE)
            this.precision = -1;
        else
            this.precision = (int)precision;
    }

    /** */
    public Order order() {
        return order;
    }

    /** */
    public int idxType() {
        return idxType;
    }

    /** */
    public int precision() {
        return precision;
    }

    /**
     * @return {@code true} if specified key's type matches to the current type, otherwise {@code false}.
     */
    public boolean validate(IndexKey key) {
        if (key == NullIndexKey.INSTANCE)
            return true;

        return idxType == key.type();
    }

    /** {@inheritDoc} */
    @Override public void writeExternal(ObjectOutput out) throws IOException {
        // Send only required info for using in MergeSort algorithm.
        out.writeInt(idxType);
        U.writeEnum(out, order.sortOrder());
    }

    /** {@inheritDoc} */
    @Override public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        idxType = in.readInt();
        order = new Order(U.readEnum(in, SortOrder.class), null);
    }
}
