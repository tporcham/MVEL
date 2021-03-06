package org.mvel.ast;

import org.mvel.ASTNode;
import org.mvel.Accessor;
import org.mvel.integration.VariableResolverFactory;
import org.mvel.optimizers.AccessorOptimizer;
import static org.mvel.optimizers.OptimizerFactory.getThreadAccessorOptimizer;

/**
 * @author Christopher Brock
 */
public class ContextDeepPropertyNode extends ASTNode {
    private transient Accessor accessor;

    public ContextDeepPropertyNode(char[] expr, int fields) {
        super(expr, fields);
    }

    public Object getReducedValueAccelerated(Object ctx, Object thisValue, VariableResolverFactory factory) {
        try {
            if (accessor != null) {
                return valRet(accessor.getValue(ctx, thisValue, factory));
            }
            else {
                AccessorOptimizer aO = getThreadAccessorOptimizer();
                accessor = aO.optimizeAccessor(name, ctx, thisValue, factory, false);
                return valRet(aO.getResultOptPass());
            }
        }
        catch (ClassCastException e) {
            return handleDynamicDeoptimization(ctx, thisValue, factory);
        }
    }

    private Object handleDynamicDeoptimization(Object ctx, Object thisValue, VariableResolverFactory factory) {
        synchronized (this) {
            accessor = null;
            return getReducedValueAccelerated(ctx, thisValue, factory);
        }
    }


    public Object getReducedValue(Object ctx, Object thisValue, VariableResolverFactory factory) {
        return getReducedValueAccelerated(ctx, thisValue, factory);
    }
}
