package medicineAPI;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import lombok.NoArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

@NoArgsConstructor

public class PdfToTextConverter
{

    public static String converter(String url, String outputPath) {
//        String url = "https://example.com/sample.pdf";
//        String outputPath = "output.pdf";
        String text = "";

        try {
            // PDF 다운로드
            downloadPDF(url, outputPath);

            // PDF를 텍스트로 변환
            text = convertPDFToText(outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }

    private static void downloadPDF(String url, String outputPath) throws IOException {
        URL pdfUrl = new URL(url);
        ReadableByteChannel channel = Channels.newChannel(new BufferedInputStream(pdfUrl.openStream()));
        FileOutputStream outputStream = new FileOutputStream(outputPath);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

        outputStream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);

        bufferedOutputStream.close();
        outputStream.close();
        channel.close();
    }

    /**
     *
     * @param pdfPath
     * @return text
     * @throws IOException
     */
    private static String convertPDFToText(String pdfPath) throws IOException {
        PDDocument document = PDDocument.load(new File(pdfPath));
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();

        return text;
    }

//    public static String extractTextFromPdf(InputStream inputStream) throws IOException
//    {
//        PDDocument document = null;
//        try {
//            document = PDDocument.load(inputStream);
//            PDFTextStripper stripper = new PDFTextStripper();
//            return stripper.getText(document);
//        } finally {
//            if (document != null) {
//                document.close();
//            }
//        }
//    }
}
