package com.bantvegas.dietnyplan.service;

import com.bantvegas.dietnyplan.util.HtmlPlanFormatter;
import com.bantvegas.dietnyplan.util.ShoppingListBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.pdf.BaseFont;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
public class PdfService {

    private final TemplateEngine templateEngine;

    public PdfService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generatePdf(String planContent) throws Exception {
        try {
            Context context = new Context();
            String structuredPlan = HtmlPlanFormatter.formatToHtml(planContent);

            Map<String, ShoppingListBuilder.Ingredient> shoppingList = ShoppingListBuilder.extractShoppingList(planContent);
            String shoppingListHtml = ShoppingListBuilder.toHtmlTable(shoppingList);

            context.setVariable("structuredPlan", structuredPlan);
            context.setVariable("shoppingListHtml", shoppingListHtml);

            String html = templateEngine.process("pdf", context);

            ITextRenderer renderer = new ITextRenderer();
            ITextFontResolver fontResolver = renderer.getFontResolver();
            fontResolver.addFont(new ClassPathResource("fonts/DejaVuSans.ttf").getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            renderer.setDocumentFromString(html);
            renderer.layout();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            renderer.createPDF(baos);

            return baos.toByteArray();

        } catch (Exception e) {
            System.err.println("!!! CHYBA PRI GENEROVANÍ PDF !!!");
            e.printStackTrace();
            throw e;
        }
    }
}