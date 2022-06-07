import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UrbanDirectory {
    private static final String FILE_NOT_FOUND = "Файл не найден или введен некорректный путь, пожалуйста проверьте правильность пути и повторите попытку";
    private static final String FILE_INCORRECT_FORMAT = "Неверный формат файла, пожалуйста укажите путь к файлам форматов .csv или .xml";
    private static final String FILE_IS_EMPTY = "Файл не содержит данных";


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nДля выхода из программы введите Q\nВведите путь файла: ");
            String enter = scanner.nextLine();
            String filePath = enter.replaceAll("\\s+", "").replaceAll("\n", "");
            if(filePath.equalsIgnoreCase("Q")) break;

            List<AddressObject> addressList = new ArrayList<>();

            //проверка формата файла
            if (filePath.endsWith(".csv")) {
                addressList = getAddressBookFromCSV(filePath);
            } else if (filePath.endsWith(".xml")) {
                addressList = getAddressBookFromXML(filePath);
            } else if (!filePath.equalsIgnoreCase("Q")) {
                System.out.println(FILE_INCORRECT_FORMAT);
            } else break;

            System.out.println("Файл содержит :" + addressList.size() + " записей");

            getDuplicateRecords(addressList);
            getInfoFloor(addressList);

        }
        scanner.close();
    }

    public static List<AddressObject> getAddressBookFromCSV(String filePath){
        List<AddressObject> addressList = new ArrayList<>();
        try {
            InputStreamReader in = new InputStreamReader(new FileInputStream(filePath), "WINDOWS-1251");
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
        return null;
    }

    // Отображает дублирующиеся записи с количеством повторений.
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
        System.out.println("Количество дублирующихся записей - " + countDuplicate);
        System.out.println();
    }

    // Получает на вход прочитанные данные из файла и отображает, сколько в каждом городе: 1, 2, 3, 4 и 5 этажных зданий.
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
}

