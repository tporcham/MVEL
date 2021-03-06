package org.mvel.ast;

import org.mvel.ASTNode;
import org.mvel.Accessor;
import org.mvel.PropertyAccessor;
import org.mvel.integration.VariableResolverFactory;
import org.mvel.optimizers.AccessorOptimizer;
import org.mvel.optimizers.OptimizerFactory;

/**
 * @author Christopher Brock
 */
public class ThisValDeepPropertyNode extends ASTNode {
    private transient Accessor accessor;

    public ThisValDeepPropertyNode(char[] expr, int fields) {
        super(expr, fields);
    }

    public Object getReducedValueAccelerated(Object ctx, Object thisValue, VariableResolverFactory factory) {
        if (accessor != null) {
            return valRet(accessor.getValue(thisValue, thisValue, factory));
        }
        else {
            AccessorOptimizer aO = OptimizerFactory.getThreadAccessorOptimizer();
            accessor = aO.optimizeAccessor(name, thisValue, thisValue, factory, false);

            return valRet(accessor.getValue(thisValue, thisValue, factory));
        }
    }

    public Object getReducedValue(Object ctx, Object thisValue, VariableResolverFactory factory) {
        return valRet(PropertyAccessor.get(name, thisValue, factory, thisValue));
    }
}
