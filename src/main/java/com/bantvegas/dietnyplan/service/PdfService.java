package com.bantvegas.dietnyplan.service;

import com.bantvegas.dietnyplan.util.HtmlPlanFormatter;
import com.bantvegas.dietnyplan.util.ShoppingListBuilder;
import com.lowagie.text.pdf.BaseFont;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Slf4j
@Service
public class PdfService {

    private final TemplateEngine templateEngine;

    public PdfService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generatePdf(String planContent) throws Exception {
        try {
            // 1. Spracuj plán
            String structuredPlan = HtmlPlanFormatter.formatToHtml(planContent);
            Map<String, ShoppingListBuilder.Ingredient> shoppingList = ShoppingListBuilder.extractShoppingList(planContent);
            String shoppingListHtml = ShoppingListBuilder.toHtmlTable(shoppingList);

            // 2. Priprav Thymeleaf HTML
            Context context = new Context();
            context.setVariable("structuredPlan", structuredPlan);
            context.setVariable("shoppingListHtml", shoppingListHtml);
            String html = templateEngine.process("pdf", context);

            // 3. Generuj PDF
            ITextRenderer renderer = new ITextRenderer();
            ITextFontResolver fontResolver = renderer.getFontResolver();

            // 4. Načítaj font ako dočasný súbor (funguje aj v jar)
            ClassPathResource fontResource = new ClassPathResource("fonts/DejaVuSans.ttf");
            File tempFont = File.createTempFile("dejavu", ".ttf");
            try (InputStream fontStream = fontResource.getInputStream()) {
                Files.copy(fontStream, tempFont.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            fontResolver.addFont(tempFont.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            // 5. Layout a vytvorenie PDF
            renderer.setDocumentFromString(html);
            renderer.layout();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            renderer.createPDF(baos);

            return baos.toByteArray();

        } catch (Exception e) {
            log.error("❌ Chyba pri generovaní PDF", e);
            throw e;
        }
    }
}
