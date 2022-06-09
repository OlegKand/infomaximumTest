import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UrbanDirectory {
    private static List<AddressObject> addressList = new ArrayList<>();

    private static final String FILE_NOT_FOUND = "Файл не найден или введен некорректный путь, пожалуйста проверьте правильность пути и повторите попытку";
    private static final String FILE_INCORRECT_FORMAT = "Неверный формат файла, пожалуйста укажите путь к файлам форматов .csv или .xml";
    private static final String FILE_IS_EMPTY = "Файл не содержит данных";


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nДля выхода из программы введите Q\nВведите путь файла: ");
            String enter = scanner.nextLine();
            String filePath = enter.replaceAll("\\s+", "").replaceAll("\n", "");

            //проверка формата файла
            if (filePath.endsWith(".csv")) {
                addressList = getAddressBookFromCSV(filePath);
                if (!addressList.isEmpty()) {
                    getDuplicateRecords(getAddressBookFromCSV(filePath));
                    getInfoFloor(getAddressBookFromCSV(filePath));
                }
            } else if (filePath.endsWith(".xml")) {
                addressList = getAddressBookFromXML(filePath);
                if (!addressList.isEmpty()) {
                    getDuplicateRecords(getAddressBookFromXML(filePath));
                    getInfoFloor(getAddressBookFromXML(filePath));
                }
            } else if (!filePath.equalsIgnoreCase("Q")){
                System.out.println(FILE_INCORRECT_FORMAT);
            } else break;
        }
        scanner.close();
    }

    public static List<AddressObject> getAddressBookFromCSV(String filePath){
        addressList.clear();
        try {
            InputStreamReader in = new InputStreamReader(new FileInputStream(filePath));
            BufferedReader reader = new BufferedReader(in);

            String str = reader.readLine();
            while ((str = reader.readLine()) != null){
                if (!str.isEmpty()){
                    String[] strings = str.split(";");
                    addressList.add(new AddressObject(
                            strings[0],
                            strings[1],
                            Integer.parseInt(strings[2]),
                            Integer.parseInt(strings[3])
                    ));
                }
            }
            in.close();
            reader.close();
        } catch (IOException exception){
            System.out.println(FILE_NOT_FOUND);
        }
        return addressList;
    }

    public static List<AddressObject> getAddressBookFromXML(String filePath) {
        addressList.clear();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            XMLHandler handler = new XMLHandler();
            parser.parse(new File(filePath), handler);

        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        } catch (IOException e){
            System.out.println(FILE_NOT_FOUND);
        } catch (NullPointerException e){
            System.out.println(FILE_IS_EMPTY);
        }

        return addressList;
    }

    // 1. Отображает дублирующиеся записи с количеством повторений.
    public static void getDuplicateRecords(List<AddressObject> list){
        Map<AddressObject, Long> duplicateList = list.stream()
                .collect(Collectors.toMap(
                        Function.identity(), v -> 1L, Long::sum
                ));
        AtomicInteger countDuplicate = new AtomicInteger(0);
        duplicateList.forEach((k,v) -> {
            if (v > 1) {
                System.out.printf("Запись %s дублируется %d раз\n", k.toString(), v);
                countDuplicate.getAndIncrement();
            }
        });
        if (list.size() != 0) {
            System.out.println("Количество дублирующихся записей - " + countDuplicate);
            System.out.println();
        }
    }

    // 2. Получает на вход прочитанные данные из файла и отображает, сколько в каждом городе: 1, 2, 3, 4 и 5 этажных зданий.
    public static void getInfoFloor(List<AddressObject> list) {

        Map<String, Map<Integer, List<AddressObject>>> groupingList = list.stream()
                .collect(
                        Collectors.groupingBy(AddressObject::getCity, Collectors.groupingBy(AddressObject::getFloor))
                ).entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));

        groupingList.forEach((k,v) -> {
            System.out.printf("В городе %s :\n", k);
            for(Map.Entry<Integer, List<AddressObject>> entry : v.entrySet()){
                System.out.printf("- зданий с количеством этажей %d - %d шт.\n", entry.getKey(), entry.getValue().size());
            }
            System.out.println();
        } );
    }

    private static class XMLHandler extends DefaultHandler {

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (qName.equals("item")) {
                byte[] cityBuffer = attributes.getValue("city").getBytes(StandardCharsets.UTF_8);
                String city = new String(cityBuffer);
                byte[] streetBuffer = attributes.getValue("street").getBytes(StandardCharsets.UTF_8);
                String street = new String(streetBuffer);
                Integer house = Integer.parseInt(attributes.getValue("house"));
                Integer floor = Integer.parseInt(attributes.getValue("floor"));
                addressList.add(new AddressObject(city, street, house, floor));
            }
        }
    }
}

