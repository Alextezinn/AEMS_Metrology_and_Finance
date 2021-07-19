Сигнатура команды запуска:
java -Dconfig=<название свойства и путь к конфигу свойств> -Dlog4j.configurationFile=<название свойства и путь к конфигу логгера> -jar <название приложения> <тип провайдера> <базовый метод> <расширяющий метод> <данные>


<тип провайдера>
 xml
 csv
 jdbc


<базовые методы>
 outfit
 analysis
 employee_management
 change_status_outfit

 
<расширяющие методы>
 create_outfit
 delete_outfit
 edit_outfit
 all_employee_outfits
 all_executor_outfits
 add_employee
 promote_employee
 demote_employee
 remove_employee
 finish_work
 change_status_to_in_work


<данные вводятся последовательно через пробел, массивы данных вводятся через запятую>
 11 21,22 33 45 50


Для начала работы программы нужно инициализировать данные
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar csv data_init
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar xml data_init
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar jdbc data_init


В data source будут присутствовать следующие данные:
id customer: 10, 11, 12, 13, 14;
id employee: 20, 21, 22, 23, 24;
id place of work: 30, 31, 32, 33, 34;
id mechanical measurement: 40, 41, 42, 43;
id electrical measurement: 44, 45, 46;
id head of department: 50, 51, 52, 53, 54;
id executor: 60, 61, 62, 63, 64;
id outfit: 1, 2, 3, 4;


создание наряда
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar csv outfit create_outfit 11 21,22 33 45 50 calibration electrical
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar xml outfit create_outfit 11 21,22 33 45 50 calibration electrical
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar jdbc outfit create_outfit 11 21,22 33 45 50 calibration electrical

редактирование наряда
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar csv outfit edit_outfit 2 50 21,24
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar xml outfit edit_outfit 2 50 21,24
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar jdbc outfit edit_outfit 2 50 21,24


получить все id нарядов где есть рабочий с таким id
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar csv analysis all_employee_outfits 22 52
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar xml analysis all_employee_outfits 22 52
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar jdbc analysis all_employee_outfits 22 52


получить все id нарядов где есть исполнитель с таким id
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar csv analysis all_executor_outfits 63 52
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar xml analysis all_executor_outfits 63 52
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar jdbc analysis all_executor_outfits 63 52


добавить сотрудника
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar csv employee_management add_employee Tito 52
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar xml employee_management add_employee Tito 52
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar jdbc employee_management add_employee Tito 52


повысить сотрудника в должности
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar csv employee_management promote_employee 20 53
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar xml employee_management promote_employee 20 53
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar jdbc employee_management promote_employee 20 53


понизить сотрудника в должности
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar csv employee_management demote_employee 21 54
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar xml employee_management demote_employee 21 54
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar jdbc employee_management demote_employee 21 54


уволить сотрудника
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar csv employee_management remove_employee 24 52
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar xml employee_management remove_employee 24 52
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar jdbc employee_management remove_employee 24 52


изменить статус наряда на "в работе"
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar csv change_outfit change_status_to_in_work 2 63
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar xml change_outfit change_status_to_in_work 2 63
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar jdbc change_outfit change_status_to_in_work 2 63


изменить статус наряда на "завершен"
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar csv change_outfit finish_work 2 63
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar xml change_outfit finish_work 2 63
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar jdbc change_outfit finish_work 2 63

удаление наряда
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar csv outfit delete_outfit 2 50
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar xml outfit delete_outfit 2 50
java -Dconfig="enviroment.properties" -Dlog4j2.configurationFile="log4j2.properties" -jar aems_metrology_and_finance.jar jdbc outfit delete_outfit 2 50


