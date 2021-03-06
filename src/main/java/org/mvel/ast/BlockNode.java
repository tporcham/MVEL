package org.mvel.ast;

import org.mvel.ASTNode;

/**
 * @author Christopher Brock
 */
public class BlockNode extends ASTNode {
    protected char[] block;

    public BlockNode(char[] expr, int fields) {
        super(expr, fields);
    }

    public BlockNode(char[] expr, int fields, char[] block) {
        super(expr, fields);
        this.block = block;
    }
}

