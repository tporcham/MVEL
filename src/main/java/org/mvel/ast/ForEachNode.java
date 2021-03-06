package org.mvel.ast;

import org.mvel.CompileException;
import org.mvel.ExecutableStatement;
import org.mvel.MVEL;
import org.mvel.integration.VariableResolverFactory;
import org.mvel.integration.impl.DefaultLocalVariableResolverFactory;
import org.mvel.integration.impl.ItemResolverFactory;
import static org.mvel.util.ParseTools.subCompileExpression;
import static org.mvel.util.ParseTools.subset;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * @author Christopher Brock
 */
public class ForEachNode extends BlockNode {
    protected String item;

    private char[] cond;
    protected ExecutableStatement condition;
    protected ExecutableStatement compiledBlock;

    private static final int COLLECTION = 0;
    private static final int ARRAY = 1;
    private static final int CHARSEQUENCE = 2;
    private static final int INTEGER = 3;
    private static final int ITERABLE = 4;

    private int type = -1;

    public ForEachNode(char[] condition, char[] block, int fields) {
        super(condition, fields);
        handleCond(condition);
        this.block = block;
        if ((fields & COMPILE_IMMEDIATE) != 0) {
            this.compiledBlock = (ExecutableStatement) subCompileExpression(block);
        }
    }

    public Object getReducedValueAccelerated(Object ctx, Object thisValue, VariableResolverFactory factory) {
        ItemResolverFactory.ItemResolver itemR = new ItemResolverFactory.ItemResolver(item);
        ItemResolverFactory itemFactory = new ItemResolverFactory(itemR, new DefaultLocalVariableResolverFactory(factory));

        Object iterCond = condition.getValue(ctx, thisValue, factory);


        if (type == -1) {
            if (iterCond instanceof Collection) {
                type = COLLECTION;
            }
            else if (iterCond instanceof Object[]) {
                type = ARRAY;
            }
            else if (iterCond instanceof CharSequence) {
                type = CHARSEQUENCE;

            }
            else if (iterCond instanceof Integer) {
                type = INTEGER;
            }
            else {
                try {
                    Class.forName("java.lang.Iterable");
                    type = ITERABLE;
                }
                catch (Exception e) {
                    throw new CompileException("non-iterable type: " + iterCond.getClass().getName());
                }
            }
        }


        switch (type) {
            case COLLECTION:
                for (Object o : (Collection) iterCond) {
                    itemR.setValue(o);
                    compiledBlock.getValue(ctx, thisValue, itemFactory);
                }
                break;
            case ARRAY:
                int len = Array.getLength(iterCond);
                for (int i = 0; i < len; i++) {
                    itemR.setValue(Array.get(iterCond, i));
                    compiledBlock.getValue(ctx, thisValue, itemFactory);
                }
                break;
            case CHARSEQUENCE:
                for (Object o : iterCond.toString().toCharArray()) {
                    itemR.setValue(o);
                    compiledBlock.getValue(ctx, thisValue, itemFactory);
                }
                break;
            case INTEGER:
                int max = (Integer) iterCond + 1;
                for (int i = 1; i != max; i++) {
                    itemR.setValue(i);
                    compiledBlock.getValue(ctx, thisValue, itemFactory);
                }
                break;

            case ITERABLE:
                for (Object o : (Iterable) iterCond) {
                    itemR.setValue(o);
                    compiledBlock.getValue(ctx, thisValue, itemFactory);
                }
                break;
        }

        return null;
    }

    public Object getReducedValue(Object ctx, Object thisValue, VariableResolverFactory factory) {

        ItemResolverFactory.ItemResolver itemR = new ItemResolverFactory.ItemResolver(item);
        ItemResolverFactory itemFactory = new ItemResolverFactory(itemR, new DefaultLocalVariableResolverFactory(factory));

        Object iterCond = MVEL.eval(cond, thisValue, factory);
        this.compiledBlock = (ExecutableStatement) subCompileExpression(block);
        
        if (iterCond instanceof Collection) {
            for (Object o : (Collection) iterCond) {
                itemR.setValue(o);
                compiledBlock.getValue(ctx, thisValue, itemFactory);
            }
        }
        else if (iterCond != null && iterCond.getClass().isArray()) {
            int len = Array.getLength(iterCond);
            for (int i = 0; i < len; i++) {
                itemR.setValue(Array.get(iterCond, i));
                compiledBlock.getValue(ctx, thisValue, itemFactory);
            }
        }
        else if (iterCond instanceof CharSequence) {
            for (Object o : iterCond.toString().toCharArray()) {
                itemR.setValue(o);
                compiledBlock.getValue(ctx, thisValue, itemFactory);
            }
        }
        else if (iterCond instanceof Integer) {
            int max = (Integer) iterCond + 1;
            for (int i = 1; i != max; i++) {
                itemR.setValue(i);
                compiledBlock.getValue(ctx, thisValue, itemFactory);
            }
        }
        else {
            try {
                Class.forName("java.lang.Iterable");
                for (Object o : (Iterable) iterCond) {
                    itemR.setValue(o);
                    compiledBlock.getValue(ctx, thisValue, itemFactory);
                }
            }
            catch (Exception e) {
                throw new CompileException("non-iterable type: " + iterCond.getClass().getName());
            }
        }

        return null;
    }

    private void handleCond(char[] condition) {
        int cursor = 0;
        while (cursor < condition.length && condition[cursor] != ':') cursor++;

        if (cursor == condition.length || condition[cursor] != ':')
            throw new CompileException("expected : in foreach");

        item = new String(condition, 0, cursor).trim();

        this.condition = (ExecutableStatement) subCompileExpression(this.cond = subset(condition, ++cursor));
    }
}
