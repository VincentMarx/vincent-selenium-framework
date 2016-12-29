/**
 * Created by Vincent on 2016/12/29.
 * Verify test step status
 */
import spock.lang.Specification
import com.vincent.core.report.RCheckPoint
import com.vincent.core.report.RTestStep
import com.vincent.core.testcase.Status

class TestStepStatusSpec extends Specification{
    def "verify test step status"(){
        RTestStep testStep = new RTestStep("Step1")
        RCheckPoint cp1 = new RCheckPoint(s1, "dummy", "dummy")
        RCheckPoint cp2 = new RCheckPoint(s2, "dummy", "dummy")
        RCheckPoint cp3 = new RCheckPoint(s3, "dummy", "dummy")
        testStep.addCheckPoint(cp1)
        testStep.addCheckPoint(cp2)
        testStep.addCheckPoint(cp3)


        expect:
        testStep.getStatus()==s4

        where:
        s1 | s2 | s3 | s4
        Status.Fail | Status.Done | Status.Pass | Status.Fail
        Status.Done | Status.Done | Status.Done | Status.Done
        Status.Fatal | Status.Done | Status.Pass | Status.Fatal
        Status.Pass | Status.Warning | Status.Pass | Status.Warning
        Status.Pass | Status.Done | Status.Stop | Status.Stop
        Status.Done | Status.Pass | Status.Pass | Status.Pass
    }

    def "verify test step status without check point"(){
        RTestStep testStep = new RTestStep("Step1")
        expect:
        testStep.getStatus()==Status.Done
    }
}