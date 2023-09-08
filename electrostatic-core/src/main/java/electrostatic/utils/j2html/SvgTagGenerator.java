package electrostatic.utils.j2html;

import j2html.attributes.Attr;

public class SvgTagGenerator {

    public static SvgTag svg() {
        return new SvgTag();
    }

    public static SvgTag svg(Attr.ShortForm shortAttr) {
        return Attr.addTo(new SvgTag(), shortAttr);
    }

    public static UseTag use() {
        return new UseTag();
    }

    public static UseTag use(Attr.ShortForm shortAttr) {
        return Attr.addTo(new UseTag(), shortAttr);
    }

    public static PathTag path() {
        return new PathTag();
    }

    public static PathTag path(Attr.ShortForm shortAttr) {
        return Attr.addTo(new PathTag(), shortAttr);
    }

    public static SymbolTag symbol() {
        return new SymbolTag();
    }

    public static SymbolTag symbol(Attr.ShortForm shortAttr) {
        return Attr.addTo(new SymbolTag(), shortAttr);
    }

}
