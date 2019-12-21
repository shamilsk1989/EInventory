package com.alhikmahpro.www.e_inventory.Data;

import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class DashedSeparator extends LineSeparator {
    /** the gap between the dots. */
    protected float gap = 2;
    //protected float customLineWidth = 2;

    /**
     * @see com.itextpdf.text.pdf.draw.DrawInterface#draw(com.itextpdf.text.pdf.PdfContentByte, float, float, float, float, float)
     */
    public void draw(PdfContentByte canvas, float llx, float lly, float urx, float ury, float y) {
        canvas.saveState();
        canvas.setLineWidth(lineWidth);
        canvas.setLineCap(PdfContentByte.LINE_JOIN_ROUND);
        canvas.setLineDash(0, gap, gap / 2);
        drawLine(canvas, llx, urx, y);
        canvas.restoreState();
    }

    /**
     * Getter for the gap between the center of the dots of the dotted line.
     * @return	the gap between the center of the dots
     */
    public float getGap() {
        return gap;
    }

    /**
     * Setter for the gap between the center of the dots of the dotted line.
     * @param	gap	the gap between the center of the dots
     */
    public void setGap(float gap) {
        this.gap = gap;
    }

}
