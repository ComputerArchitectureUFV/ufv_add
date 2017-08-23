package add.dataflow.sync;

import hades.models.StdLogic1164;
import hades.models.StdLogicVector;
import hades.signals.Signal;
import hades.signals.SignalStdLogic1164;
import hades.simulator.SimEvent;
import hades.simulator.SimEvent1164;

/**
 * Merge component for the ADD Accelerator Design and Deploy.<br>
 * The component is responsible for choosing which of the inputs to pass to the
 * output depending on the value of R_IN1 and R_IN2.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class Merge extends GenericBin {

    /**
     * Object Constructor.
     */
    public Merge() {
        super();
        setCompName("MERGE");
    }

    /**
     * evaluate(): called by the simulation engine on all events that concern
     * this object. The object is responsible for updating its internal state
     * and for scheduling all pending output events. In this case, it will be
     * checked if any of the R_IN (1 or 2) inputs is at high level and put the
     * respective input value in the output. If the two R_IN signals are at high
     * level, the value of input 1 will be set to the output. If both are 0,
     * nothing will be done.
     *
     * @param arg an arbitrary object argument
     */
    @Override
    public void evaluate(Object arg) {
        double time;

        Signal signalClk = null, signalRst = null, signalEn = null;
        Signal signalRin1 = null, signalRin2 = null, signalRout = null;
        Signal signalDin1 = null, signalDin2 = null, signalDout = null;

        //código para tick_up e Tick_down
        if ((signalClk = getPortClk().getSignal()) != null) {
            SignalStdLogic1164 tick = (SignalStdLogic1164) getPortClk().getSignal();
            if (tick.hasRisingEdge()) {
                tickUp();
            } else if (tick.hasFallingEdge()) {
                tickDown();
            }
        }
        //Fim

        boolean isX = false;

        if ((signalClk = getPortClk().getSignal()) == null) {
            isX = true;
        } else if ((signalRst = getPortRst().getSignal()) == null) {
            isX = true;
        } else if ((signalEn = getPortEn().getSignal()) == null) {
            isX = true;
        } else if ((signalRin1 = getPortRin1().getSignal()) == null) {
            isX = true;
        } else if ((signalRin2 = getPortRin2().getSignal()) == null) {
            isX = true;
        } else if ((signalDin1 = getPortDin1().getSignal()) == null) {
            isX = true;
        } else if ((signalDin2 = getPortDin2().getSignal()) == null) {
            isX = true;
        } else if ((signalDout = getPortDout().getSignal()) == null) {
            isX = true;
        }

        StdLogic1164 valueRst = getPortRst().getValueOrU();
        StdLogic1164 rOut;

        if (isX || valueRst.is_1()) {
            reseted();

            //para portDout
            if ((signalDout = getPortDout().getSignal()) != null) { // get output
                vector = vector_UUU.copy();
                time = simulator.getSimTime() + delay;
                simulator.scheduleEvent(new SimEvent(signalDout, time, vector, getPortDout()));
            }

            //para portRout
            if ((signalRout = getPortRout().getSignal()) != null) { // get output
                rOut = new StdLogic1164(2);
                time = simulator.getSimTime() + delay;
                simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout, time, rOut, getPortRout()));
            }
        } else {

            SignalStdLogic1164 clk = (SignalStdLogic1164) getPortClk().getSignal();
            StdLogic1164 en = getPortEn().getValueOrU();
            StdLogic1164 rIn1 = getPortRin1().getValueOrU();
            StdLogic1164 rIn2 = getPortRin2().getValueOrU();

            if (en.is_1()) {
                if (clk.hasRisingEdge()) {

                    signalDin1 = getPortDin1().getSignal();
                    StdLogicVector dIn1 = (StdLogicVector) signalDin1.getValue();

                    signalDin2 = getPortDin2().getSignal();
                    StdLogicVector dIn2 = (StdLogicVector) signalDin2.getValue();

                    StdLogicVector saida = new StdLogicVector(32);

                    if (rIn1.is_1()) {
                        saida.setValue(compute((int) dIn1.getValue(), (int) dIn2.getValue()));//aqui ocorre a chamada para a computação da saída.
                        vector = saida.copy();
                        time = simulator.getSimTime() + delay;
                        simulator.scheduleEvent(new SimEvent(signalDout, time, vector, getPortDout()));
                        if ((signalRout = getPortRout().getSignal()) != null) { // get output
                            rOut = new StdLogic1164(3);
                            time = simulator.getSimTime() + delay;
                            simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout, time, rOut, getPortRout()));
                        }
                    } else if (rIn2.is_1()) {
                        saida.setValue(compute((int) dIn2.getValue(), (int) dIn1.getValue()));//aqui ocorre a chamada para a computação da saída.
                        vector = saida.copy();
                        time = simulator.getSimTime() + delay;
                        simulator.scheduleEvent(new SimEvent(signalDout, time, vector, getPortDout()));
                        if ((signalRout = getPortRout().getSignal()) != null) { // get output
                            rOut = new StdLogic1164(3);
                            time = simulator.getSimTime() + delay;
                            simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout, time, rOut, getPortRout()));
                        }
                    } else {
                        if ((signalRout = getPortRout().getSignal()) != null) { // get output
                            rOut = new StdLogic1164(2);
                            time = simulator.getSimTime() + delay;
                            simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout, time, rOut, getPortRout()));
                        }
                        notCompute();
                    }
                }
            }
        }
    }
}
