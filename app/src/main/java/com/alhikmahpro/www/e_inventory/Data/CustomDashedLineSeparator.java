package com.alhikmahpro.www.e_inventory.Data;

import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

public class CustomDashedLineSeparator extends DottedLineSeparator {
    protected float dash = 30;
    protected float phase = 4.5f;

    public float getDash() {
        return dash;
    }

    public float getPhase() {
        return phase;
    }

    public void setDash(float dash) {
        this.dash = dash;
    }

    public void setPhase(float phase) {
        this.phase = phase;
    }

    public void draw(PdfContentByte canvas,
                     float llx, float lly, float urx, float ury, float y) {
        canvas.saveState();
        canvas.setLineWidth(lineWidth);
        canvas.setLineDash(dash, gap, phase);
        drawLine(canvas, llx, urx, y);
        canvas.restoreState();
    }
}
