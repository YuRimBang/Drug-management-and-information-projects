package medicineAPI;

import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

public interface Api<T>
{
    public String pullMed(String page, String row, String num, String name) throws IOException;
    public List<T> parsingJson(String str) throws ParseException, ParserConfigurationException, IOException, SAXException;

}
