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
            String structuredPlan = HtmlPlanFormatter.formatToHtml(planContent);
            Map<String, ShoppingListBuilder.Ingredient> shoppingList = ShoppingListBuilder.extractShoppingList(planContent);
            String shoppingListHtml = ShoppingListBuilder.toHtmlTable(shoppingList);

            Context context = new Context();
            context.setVariable("structuredPlan", structuredPlan);
            context.setVariable("shoppingListHtml", shoppingListHtml);
            context.setVariable("shoppingItemCount", shoppingList.size());

            // dočasné hodnoty, v ostrej verzii vlož z DietRequest
            context.setVariable("name", "Martina");
            context.setVariable("goal", "schudnúť");
            context.setVariable("weight", 78);

            String html = templateEngine.process("pdf", context);
            log.debug("📄 Generated HTML:\n{}", html);

            ITextRenderer renderer = new ITextRenderer();
            ITextFontResolver fontResolver = renderer.getFontResolver();

            ClassPathResource fontResource = new ClassPathResource("fonts/DejaVuSans.ttf");
            File tempFont = File.createTempFile("dejavu", ".ttf");
            try (InputStream fontStream = fontResource.getInputStream()) {
                Files.copy(fontStream, tempFont.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            fontResolver.addFont(tempFont.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
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
