package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import ru.otus.hw.domain.Student;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.service.LocalizedIOService;
import ru.otus.hw.service.ResultService;
import ru.otus.hw.service.StudentService;
import ru.otus.hw.service.TestService;

@ShellComponent(value = "Shell commands")
@RequiredArgsConstructor
public class ShellCommand {

    private final StudentService studentService;

    private final LocalizedIOService ioService;

    private final TestService testService;

    private final ResultService resultService;

    private Student student;

    @ShellMethod(value = "Login student", key = {"l", "login"})
    public void login() {
        student = studentService.determineCurrentStudent();
        ioService.printFormattedLineLocalized("ShellCommand.student.login", student.getFullName());
    }

    @ShellMethod(value = "Run test", key = {"t", "test"})
    @ShellMethodAvailability(value = "isLoginStudent")
    public void runTest() {
        try {
            var testResult = testService.executeTestFor(student);
            resultService.showResult(testResult);
            cleanStudent();
        } catch (QuestionReadException ex) {
            ioService.printLineLocalized("TestRunnerService.error.reading.questions");
        } catch (IllegalArgumentException ex) {
            ioService.printLineLocalized("TestRunnerService.error.reading.value");
        } catch (Exception ex) {
            ioService.printLineLocalized("TestRunnerService.error.unknown");
        }
    }

    private Availability isLoginStudent() {
        return student != null ?
                Availability.available() :
                Availability.unavailable(ioService.getMessage("ShellCommand.error.login"));
    }

    private void cleanStudent() {
        student = null;
    }
}
