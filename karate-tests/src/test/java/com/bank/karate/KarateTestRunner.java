package com.bank.karate;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Runner principal para ejecutar todos los tests de Karate.
 */
public class KarateTestRunner {

    // Tag para seleccionar tests (excluye @ignore)
    private static final String TAGS = "~@ignore";

    @Test
    void runAllTests() {
        Results results = Runner.path("classpath:com/bank/karate/features")
                .outputCucumberJson(true)
                .outputJunitXml(true)
                .tags(TAGS)
                .parallel(1); // Ejecutar secuencialmente

        // Verificar que no haya fallos
        assertEquals(0, results.getFailCount(), 
            results.getErrorMessages());
    }

    @Test
    void runCustomerTests() {
        Results results = Runner.path("classpath:com/bank/karate/features/customer")
                .outputCucumberJson(true)
                .tags(TAGS)
                .parallel(1);

        assertEquals(0, results.getFailCount(), 
            results.getErrorMessages());
    }

    @Test
    void runSmokeTests() {
        Results results = Runner.path("classpath:com/bank/karate/features")
                .outputCucumberJson(true)
                .tags("@smoke")
                .parallel(1);

        assertEquals(0, results.getFailCount(), 
            results.getErrorMessages());
    }

    @Test
    void runE2ETests() {
        Results results = Runner.path("classpath:com/bank/karate/features/e2e")
                .outputCucumberJson(true)
                .tags(TAGS)
                .parallel(1);

        assertEquals(0, results.getFailCount(), 
            results.getErrorMessages());
    }
}
