package com.github.welcome_to_school_manager.helpers.utility;
import android.util.Log;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class WaterMark extends PdfPageEventHelper {
    private Image image;

    public WaterMark(Image image) {
        this.image = image;
    }

    public WaterMark() {}
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        super.onEndPage(writer, document);
        PdfContentByte contentByte = writer.getDirectContentUnder();
        image.scalePercent(15);
        float positionY = (writer.getPageSize().getTop()/2)-(image.getScaledHeight()/2);
        float positionX = (writer.getPageSize().getRight()/2)-(image.getScaledWidth()/2);
        Log.e("Positions: ", "Y: "+positionY+" X: "+positionX);
        image.setAbsolutePosition(positionX, positionY);
        PdfGState state = new PdfGState();
        state.setFillOpacity(0.3f);
        contentByte.setGState(state);
        try {
            contentByte.addImage(image);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
