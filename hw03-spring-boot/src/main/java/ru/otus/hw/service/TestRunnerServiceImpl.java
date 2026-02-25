package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.QuestionReadException;

@Service
@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    private final LocalizedIOService ioService;

    @Override
    public void run() {
        try {
            var student = studentService.determineCurrentStudent();
            var testResult = testService.executeTestFor(student);
            resultService.showResult(testResult);
        } catch (QuestionReadException ex) {
            ioService.printLineLocalized("TestRunnerService.error.reading.questions");
        } catch (IllegalArgumentException ex) {
            ioService.printLineLocalized("TestRunnerService.error.reading.value");
        } catch (Exception ex) {
            ioService.printLineLocalized("TestRunnerService.error.unknown");
        }
    }
}
