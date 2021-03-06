package org.mvel.optimizers;

import org.mvel.AbstractParser;
import static org.mvel.util.PropertyTools.isIdentifierPart;

import static java.lang.Character.isWhitespace;
import static java.lang.Thread.currentThread;
import java.lang.reflect.Method;

/**
 * @author Christopher Brock
 */
public class AbstractOptimizer extends AbstractParser {
    protected static final int BEAN = 0;
    protected static final int METH = 1;
    protected static final int COL = 2;

    protected int start = 0;

      protected Object tryStaticAccess() {
        int begin = cursor;
        try {
            /**
             * Try to resolve this *smartly* as a static class reference.
             *
             * This starts at the end of the token and starts to step backwards to figure out whether
             * or not this may be a static class reference.  We search for method calls simply by
             * inspecting for ()'s.  The first union area we come to where no brackets are present is our
             * test-point for a class reference.  If we find a class, we pass the reference to the
             * property accessor along  with trailing methods (if any).
             *
             */
            boolean meth = false;
            //  int depth = 0;
            int last = length;
            for (int i = length - 1; i > 0; i--) {
                switch (expr[i]) {
                    case '.':
                        if (!meth) {
                            try {
                                return currentThread().getContextClassLoader().loadClass(new String(expr, 0, cursor = last));
                            }
                            catch (ClassNotFoundException e) {
                                Class cls = currentThread().getContextClassLoader().loadClass(new String(expr, 0, i));
                                String name = new String(expr, i + 1, expr.length - i - 1);
                                try {
                                    return cls.getField(name);
                                }
                                catch (NoSuchFieldException nfe) {
                                    for (Method m : cls.getMethods()) {
                                        if (name.equals(m.getName())) return m;
                                    }
                                    return null;
                                }
                            }
                        }

                        meth = false;
                        last = i;
                        break;

                    case ')':
                        i--;

                        for (int d = 1; i > 0 && d != 0; i--) {
                            switch (expr[i]) {
                                case ')':
                                    d++;
                                    break;
                                case '(':
                                    d--;
                                    break;
                                case '"':
                                case '\'':
                                    char s = expr[i];
                                    while (i > 0 && (expr[i] != s && expr[i - 1] != '\\')) i--;
                            }
                        }

                        meth = true;

                        last = i++;

                        break;


                    case '\'':
                        while (--i > 0) {
                            if (expr[i] == '\'' && expr[i - 1] != '\\') {
                                break;
                            }
                        }
                        break;

                    case '"':
                        while (--i > 0) {
                            if (expr[i] == '"' && expr[i - 1] != '\\') {
                                break;
                            }
                        }
                        break;
                }
            }
        }
        catch (Exception cnfe) {
            cursor = begin;
        }

        return null;
    }

    protected int nextSubToken() {
        skipWhitespace();

        switch (expr[start = cursor]) {
            case '[':
                return COL;
            case '.':
                skipWhitespace();
                cursor = ++start;

        }

        //noinspection StatementWithEmptyBody
        while (++cursor < length && isIdentifierPart(expr[cursor])) ;

        if (cursor != length) {
            skipWhitespace();
            switch (expr[cursor]) {
                case '[':
                    return COL;
                case '(':
                    return METH;
                default:
                    return 0;
            }
        }

        return 0;
    }

    protected String capture() {
        /**
         * Trim off any whitespace.
         */
        return new String(expr, start = trimRight(start), trimLeft(cursor) - start);
    }

    protected void whiteSpaceSkip() {
        if (cursor < length)
            //noinspection StatementWithEmptyBody
            while (isWhitespace(expr[cursor]) && ++cursor != length) ;
    }

    protected boolean scanTo(char c) {
        for (; cursor != length; cursor++) {
            if (expr[cursor] == c) {
                return true;
            }
        }
        return false;
    }

    protected int containsStringLiteralTermination() {
        int pos = cursor;
        for (pos--; pos != 0; pos--) {
            if (expr[pos] == '\'' || expr[pos] == '"') return pos;
            else if (!isWhitespace(expr[pos])) return pos;
        }
        return -1;
    }
}
