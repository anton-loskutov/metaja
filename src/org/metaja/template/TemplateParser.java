package org.metaja.template;

import java.util.ArrayList;
import java.util.List;


public class TemplateParser {

    public interface TemplateWriter<E extends Exception> {
        enum WriteType {
            OUT,
            MACRO,
            MACRO_OUT,
            MACRO_VALUE,
        }

        void setWriteType(WriteType type) throws E;

        void write(char c) throws E;
    }

    public static <E extends Exception, T extends TemplateWriter<E>> T parse(String templateString, T t) throws E {
        for (Region region : split(templateString.trim())) {
            if (region.macro) {
                boolean escape = false;
                boolean diez = false;
                boolean buks = false;
                t.setWriteType(TemplateWriter.WriteType.MACRO);
                for (int i = 0; i < region.content.length(); i++) {
                    char c = region.content.charAt(i);
                    if (!escape) {
                        if (c == DIEZ) {
                            if (buks) {
                                throw new IllegalArgumentException();
                            }
                            if (diez) {
                                t.setWriteType(TemplateWriter.WriteType.MACRO);
                                diez = false;
                            } else {
                                t.setWriteType(TemplateWriter.WriteType.MACRO_OUT);
                                diez = true;
                            }
                        } else if (c == BUKS) {
                            if (buks) {
                                if (diez) {
                                    t.setWriteType(TemplateWriter.WriteType.MACRO_OUT);
                                } else {
                                    t.setWriteType(TemplateWriter.WriteType.MACRO);
                                }
                                buks = false;
                            } else {
                                t.setWriteType(TemplateWriter.WriteType.MACRO_VALUE);
                                buks = true;
                            }
                        } else if (c == ESCP) {
                            escape = true;
                        } else {
                            t.write(c);
                        }
                    } else {
                        t.write(c);
                        escape = false;
                    }
                }

            } else if (region.content.length() > 0) {
                t.setWriteType(TemplateWriter.WriteType.OUT);
                for (int i = 0, l = region.content.length(); i < l; i++) {
                    t.write(region.content.charAt(i));
                }
            }
        }
        return t;
    }

    // ====== private =======

    private static final char DIEZ = '#';
    private static final char BUKS = '$';
    private static final char ESCP = '\\';

    private static final String O = "/***";
    private static final String C = "***/";

    private static final class Region {
        public final boolean macro;
        public final String content;

        public Region(String content, boolean macro) {
            this.content = content;
            this.macro = macro;
        }

        @Override
        public String toString() {
            return macro ? "M[" + content + "]" : "[" + content + "]";
        }
    }

    private static List<Region> split(String s) {
        ArrayList<Region> regions = new ArrayList<>();
        int fr = s.indexOf(O);
        int to = 0;
        boolean comment = false;
        while (fr != -1) {
            if (!comment) {
                regions.add(new Region(s.substring(to, fr), false));
            } else {
                StringBuilder lines = new StringBuilder();
                for (int i = to; i <fr; i++) {
                    if (s.charAt(i) == '\n') {
                        lines.append('\n');
                    }
                }
                regions.add(new Region(lines.toString(), false));

            }
            to = s.indexOf(C, fr);
            if (fr + 1 == to) { /***/
                to += C.length();
                comment = !comment;
            } else if (s.charAt(to - 1) == '/') { /*** /***/
                if (s.charAt(to - 2) == '/') { /*** //***/
                    regions.add(new Region(s.substring(fr + O.length(), to - 2), true));
                    to += C.length();
                    while (Character.isWhitespace(s.charAt(to))) {
                        to++;
                    }
                    while (!Character.isWhitespace(s.charAt(to))) {
                        to++;
                    }
                    comment = false;
                } else {
                    regions.add(new Region(s.substring(fr + O.length(), to - 1), true));
                    to += C.length();
                    comment = true;
                }
            } else if (fr + O.length() <= to) { /*** ***/
                regions.add(new Region(s.substring(fr + O.length(), to), true));
                to += C.length();
                comment = false;
            }
            fr = s.indexOf(O, to);
        }
        if (!comment) {
            regions.add(new Region(s.substring(to, s.length()), false));
        }
        return regions;
    }
}
