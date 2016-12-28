/**
 * Created by Administrator on 2016/12/28.
 * My first spock test
 */

import spock.lang.Specification
import com.vincent.core.report.RTestCase
import com.vincent.core.report.RTestStep
import com.vincent.core.testcase.Status

class TestCaseStatusSpec extends Specification{
    def "verify test case status"(){
         RTestCase testCase = new RTestCase("t1")
         RTestStep step1 = new RTestStep("s1")
         RTestStep step2 = new RTestStep("s2")
         RTestStep step3 = new RTestStep("s3")
        testCase.addStep(step1)
        testCase.addStep(step2)
        testCase.addStep(step3)
        step1.setStatus(s1)
        step2.setStatus(s2)
        step3.setStatus(s3)
        testCase.getStatus()

        expect:
        testCase.status.equals(s4)

        where:
        s1 | s2 | s3 | s4
        Status.Awaiting | Status.Done | Status.Pass | Status.Pass
        Status.Awaiting | Status.Awaiting | Status.Awaiting | Status.Awaiting
        Status.Fatal | Status.Awaiting | Status.Awaiting | Status.Fatal
        Status.Pass | Status.Warning | Status.Pass | Status.Warning
        Status.Done | Status.Pass | Status.Fatal | Status.Fatal
        Status.Warning | Status.Pass | Status.Pass | Status.Warning
    }
}