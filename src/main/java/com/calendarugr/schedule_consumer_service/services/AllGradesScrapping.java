package com.calendarugr.schedule_consumer_service.services;


import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.calendarugr.schedule_consumer_service.entities.ClassInfo;
import com.calendarugr.schedule_consumer_service.entities.Grade;
import com.calendarugr.schedule_consumer_service.entities.Group;
import com.calendarugr.schedule_consumer_service.entities.Subject;
import com.calendarugr.schedule_consumer_service.repositories.ClassInfoRepository;
import com.calendarugr.schedule_consumer_service.repositories.GradeRepository;
import com.calendarugr.schedule_consumer_service.repositories.GroupRepository;
import com.calendarugr.schedule_consumer_service.repositories.SubjectRepository;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Service
public class AllGradesScrapping {

    private static final String ROOT_URL = "https://grados.ugr.es/";

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ClassInfoRepository classInfoRepository;

    private Connection connection = Jsoup.newSession();

    private String getDay(String day) {

        switch (day) {
            case "1":
                return "Lunes";
            case "2":
                return "Martes";
            case "3":
                return "Miércoles";
            case "4":
                return "Jueves";
            case "5":
                return "Viernes";
            default:
                return "Sin día";
        }
    }

    private Document connect(String url) {

        Document doc = null;

        try {
            doc = connection.url(url).get();
        } catch (HttpStatusException e) {
            System.out.println("Error: " + e.getStatusCode());
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return doc;
    }

    // Master method

    @Async
    @Scheduled(cron = "0 50 23 * * ?") // Every day at 23:50
    public void runAllTasks() {
        // Medir el tiempo de ejecución
        long startTime = System.currentTimeMillis();
        System.out.println("Starting the scrapping process...");
        getGrades();
        getSubjects();
        getAllScheduleInfo();
        // Should I do an specific method for the "Grado en Farmacia y Nutrición Humana y Dietética", which has a broken link?
        long endTime = System.currentTimeMillis();
        System.out.println(
                "The scrapping process has finished. It took " + (endTime - startTime) / 1000 / 60 + " minutes."); // ~
                                                                                                                   // 53
                                                                                                                   // minutes
    }

    public void getGrades() {

        Document fields_doc = connect(ROOT_URL + "ramas/");

        if (fields_doc == null) {
            System.out.println("Error: Could not connect to the URL " + ROOT_URL + "ramas/");
            return;
        }

        Elements fields = fields_doc.select(".ramas a");

        for (Element field : fields) {
            String field_url = field.attr("href");

            Document grades_doc = connect(ROOT_URL + field_url);

            if (grades_doc == null) {
                System.out.println("Error: Could not connect to the URL " + ROOT_URL + field_url);
                return;
            }

            Elements grades = grades_doc.select(".lista-rama a");
            if (grades.isEmpty()) {
                grades = grades_doc.select(
                        ".clearfix.text-formatted.field.field--name-field-rich-text-text.field--type-text-long.field--label-hidden.field__item a"); // Grados
            }

            for (Element grade : grades) {
                String grade_url = grade.attr("href");
                String grade_name = grade.text();

                Optional<Grade> gradeOptional = gradeRepository.findByName(grade_name);

                if (gradeOptional.isEmpty()) {
                    Grade newGrade = new Grade();
                    newGrade.setName(grade_name);
                    newGrade.setUrl(grade_url);
                    newGrade.setField(field.text());
                    gradeRepository.save(newGrade);
                }
            }
        }
    }

    public void getSubjects() {

        List<Grade> grades = gradeRepository.findAll();

        for (Grade grade : grades) {

            Document subjects_doc = connect(grade.getUrl());

            if (subjects_doc == null) {
                System.out.println("Error: Could not connect to the URL " + grade.getUrl());
                return;
            }

            if (grade.getFaculty() == null) {
                Elements faculty = subjects_doc.select(".informacion-grado tbody tr a");
                //grade.setFaculty(faculty.text());

                for (Element fac : faculty) {

                    if (grade.getFaculty() == null) {
                        grade.setFaculty(fac.text());
                    } else {
                        grade.setFaculty(grade.getFaculty() + ", " + fac.text());
                    }

                }

                gradeRepository.save(grade);
            }

            Elements subjects = subjects_doc.select(".asignatura a");

            for (Element subject : subjects) {
                String subject_url = subject.attr("href");
                String subject_name = subject.text();

                Optional<Subject> subjectOptional = subjectRepository.findByNameAndGrade(subject_name, grade);

                if (subjectOptional.isEmpty()) {
                    Subject newSubject = new Subject();
                    newSubject.setName(subject_name);
                    newSubject.setUrl(subject_url);
                    newSubject.setGrade(grade);
                    subjectRepository.save(newSubject);
                }
            }
        }
    }

    public void getAllScheduleInfo() {

        List<Subject> subjects = subjectRepository.findAll();

        for (Subject subject : subjects) {

            Document doc = connect(subject.getUrl());

            if (doc == null) {
                System.out
                        .println("Error: No se ha podido conectar con la página de la asignatura" + subject.getName());
                return;
            }

            // First part : General information

            Elements generalInfo = doc.select(".datos-asignatura tbody tr td");

            subject.setAcademic_course(generalInfo.get(0).text().trim());
            subject.setYear(generalInfo.get(2).text().trim());
            subject.setSemester(generalInfo.get(3).text().trim());
            subject.setType(generalInfo.get(4).text().trim());
            // Sometimes the department elements give us random characters, so we have to
            // limit the size to 400 characters with a '.' at the end
            String department = generalInfo.get(6).text().trim();
            if (department.length() > 400) {
                department = department.substring(0, 400) + ".";
            }
            subject.setDepartment(department);
            subjectRepository.save(subject);

            // Second part : Teacher-Group

            Elements teachers = doc.select(".profesores");

            for (Element teacherElement : teachers) {
                Elements groups = teacherElement.select(".profesor");

                for (Element groupElement : groups) {
                    String teacher = groupElement.select("a").text();
                    String groupText = groupElement.select(".grupos").text();
                    // First "G" of Grupo out
                    groupText = groupText.substring(1);
                    String[] groupsArray = groupText.replaceAll("[^A-Z0-9]", " ").trim().split("\\s+");

                    for (String groupName : groupsArray) {
                        Optional<Group> groupOptional = groupRepository.findByNameAndSubject(groupName, subject);
                        Group group;
                        if (groupOptional.isEmpty()) {
                            group = new Group();
                            group.setName(groupName);
                            group.setSubject(subject);
                            group.setTeacher(teacher); // At first only one teacher
                            groupRepository.save(group);
                        } else {
                            group = groupOptional.get();
                            List<String> teachersList = new ArrayList<>(group.getTeachersList());
                            if (!teachersList.contains(teacher)) {
                                teachersList.add(teacher);
                                group.setTeachersList(teachersList);
                                groupRepository.save(group);
                            }
                        }
                    }
                }
            }

            // Third part : Schedule

            Elements classes = doc.select(".clase");

            if (classes.size() != 0) { // TFG has no schedule

                for (Element classElement : classes) {

                    // classes are identified by the class ".clase" and the day, for example "dia-5" its friday

                    String day = this.getDay(classElement.className().split("-")[1]);
                    String group = classElement.select(".grupo").text().replace("Grupo:", "").trim();

                    Elements moreInfo = classElement.select(".otros-datos");
                    String moreInfoText = moreInfo.text();

                    // Extracting data from moreInfoText
                    String classroom = moreInfoText.split("Aula: ")[1].split(" ")[0];
                    String initDate = moreInfoText.split("Fecha de inicio: ")[1].split(" ")[0];
                    String finishDate = moreInfoText.split("Fecha final: ")[1].split(" ")[0];

                    String schedule = moreInfoText.split("Horario: ")[1];
                    String initHour = schedule.split(" ")[1];
                    String finishHour = schedule.split(" ")[3];

                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                    Optional<Group> groupOptional = groupRepository.findByNameAndSubject(group, subject);
                    // First we check if the group exists in the "Groups section" in the web, if
                    // not, we create it. Sometimes the web doesn't show the group, so we have to.
                    if (groupOptional.isEmpty()) {

                        Group newGroup = new Group();
                        newGroup.setName(group);
                        newGroup.setSubject(subject);
                        newGroup.setTeacher("No asignado");
                        groupRepository.save(newGroup);
                        groupOptional = groupRepository.findByNameAndSubject(group, subject);

                    }

                    List<ClassInfo> listClases = classInfoRepository
                            .findByDayAndInitHourAndInitDateAndFinishDateAndClassroomAndSubjectGroup(day,
                                    java.time.LocalTime.parse(initHour, timeFormatter),
                                    java.time.LocalDate.parse(initDate, dateFormatter),
                                    java.time.LocalDate.parse(finishDate, dateFormatter), classroom,
                                    groupOptional.get());

                    System.out
                            .println("SUBJECT " + subject.getName() + " OF THE GRADE " + subject.getGrade().getName());

                    if (listClases.size() == 0) {

                        ClassInfo classInfo = new ClassInfo();
                        classInfo.setDay(day);
                        classInfo.setSubjectGroup(groupOptional.get());
                        classInfo.setClassroom(classroom);
                        classInfo.setInitDate(java.time.LocalDate.parse(initDate, dateFormatter));
                        classInfo.setFinishDate(java.time.LocalDate.parse(finishDate, dateFormatter));
                        classInfo.setInitHour(java.time.LocalTime.parse(initHour, timeFormatter));
                        classInfo.setFinishHour(java.time.LocalTime.parse(finishHour, timeFormatter));
                        classInfoRepository.save(classInfo);
                    }

                }

            }

        }

    }

}
