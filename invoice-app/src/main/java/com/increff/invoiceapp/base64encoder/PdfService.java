package com.increff.invoiceapp.base64encoder;

import com.increff.invoiceapp.models.InvoiceItem;
import com.increff.invoiceapp.models.InvoiceList;

import java.net.Inet4Address;
import java.util.Base64;

import com.increff.invoiceapp.utils.DoubleUtil;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class PdfService {

    private static final Logger logger = Logger.getLogger(PdfService.class);
    private static final String RESOURCES_DIR =
            System.getProperty("user.dir") + "/src/main/resources/com/increff/posapp";
    public static String getBase64String(
            String date,
            Integer orderId,
            List<Integer> snos,
            List<String> productNames,
            List<Integer> quantities,
            List<String> mrps,
            List<String> sellingPrices)
            throws IOException, TransformerException, JAXBException, FOPException {
//          Create the XML file
            writeInvoiceToXml(
                    date,
                    orderId,
                    snos,
                    productNames,
                    quantities,
                    mrps,
                    sellingPrices,
                    RESOURCES_DIR + "/invoice.xml");
            logger.info("XML file created");

//          Setup FOP
            FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

            //Set up a buffer to obtain the content length
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(
                    new StreamSource(new File(RESOURCES_DIR + "/invoice.xsl"))
            );

            //Make sure the XSL transformation's result is piped through to FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            //Setup input
            Source src = new StreamSource(new File(RESOURCES_DIR + "/invoice.xml"));

            //Start the transformation and rendering process
            transformer.transform(src, res);

            byte[] bytes = out.toByteArray();
            logger.info("Bytes: >> : "+bytes.toString());
            out.flush();
            out.close();

        return Base64.getEncoder().encodeToString(bytes);
    }

    public static void writeInvoiceToXml(
            String date,
            Integer orderId,
            List<Integer> snos,
            List<String> productNames,
            List<Integer> quantities,
            List<String> mrps,
            List<String> sellingPrices,
            String fileName) throws JAXBException, IOException, TransformerException {
        JAXBContext jaxbContext = JAXBContext.newInstance(InvoiceList.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");

        // Writing the data
        InvoiceList invoiceList = new InvoiceList();
        Integer size = snos.size();
        for (int i = 0; i < size; i++) {
            InvoiceItem invoiceItem = new InvoiceItem();
            invoiceItem.setSno(snos.get(i));
            invoiceItem.setProductName(productNames.get(i));
            invoiceItem.setQuantity(quantities.get(i));
            invoiceItem.setMrp(mrps.get(i));
            invoiceItem.setSellingPrice(sellingPrices.get(i));
            invoiceItem.setSubTotal(DoubleUtil.roundToString(quantities.get(i) * Double.parseDouble(sellingPrices.get(i))));
            invoiceList.getItems().add(invoiceItem);
        }
        invoiceList.setDate(date);
        invoiceList.setOrderId(orderId);
        invoiceList.setTotal(DoubleUtil.roundToString(getTotal(quantities, sellingPrices)));

        StringWriter sw = new StringWriter();

        jaxbMarshaller.marshal(invoiceList, sw);
        String xmlContent = sw.toString();

        logger.info("XML Content >>> "+ xmlContent);

        File path = new File(fileName);

        //passing file instance in filewriter
        FileWriter wr = new FileWriter(path);

        //calling writer.write() method with the string
        wr.write(xmlContent);

        //flushing the writer
        wr.flush();

        //closing the writer
        wr.close();

//        sw.flush();
//
//        sw.close();
    }

    private static Double getTotal(List<Integer> quantities, List<String> sellingPrices){
        Double total = 0.0;
        for(int i=0; i < quantities.size(); i++){
            total += Double.parseDouble(sellingPrices.get(i)) * quantities.get(i);
        }
        return DoubleUtil.round(total, 2);
    }
    private static byte[] convertStream(ByteArrayOutputStream out, Charset encoding) throws IOException {
        ByteArrayInputStream original = new ByteArrayInputStream(out.toByteArray());
        InputStreamReader contentReader = new InputStreamReader(original, encoding);
        logger.info("Default charset: "+Charset.defaultCharset());
        logger.info("Encoding: "+contentReader.getEncoding());

        int readCount;
        char[] buffer = new char[4096];
        try (ByteArrayOutputStream converted = new ByteArrayOutputStream()) {
            try (Writer writer = new OutputStreamWriter(converted, StandardCharsets.UTF_8)) {
                while ((readCount = contentReader.read(buffer, 0, buffer.length)) != -1) {
                    writer.write(buffer, 0, readCount);
                }
            }
            return converted.toByteArray();
        }
    }
}
