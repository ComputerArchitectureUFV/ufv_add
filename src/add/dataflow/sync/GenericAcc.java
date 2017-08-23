package add.dataflow.sync;

import hades.models.StdLogic1164;
import hades.models.StdLogicVector;
import hades.signals.Signal;
import hades.signals.SignalStdLogic1164;
import hades.simulator.SimEvent;
import hades.simulator.SimEvent1164;

/**
 * GenericAcc component for the ADD Accelerator Design and Deploy.<br>
 * The component implements a generic accumulator.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class GenericAcc extends GenericI {

    private int acc;
    private int counter;

    /**
     * Object Constructor.
     */
    public GenericAcc() {
        super();
        acc = 0;
        setImmediate(getId());
        counter = getImmediate();
        setCompName("Generic_ACC");
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level.In this case it clears the text displayed by the component and de
     * accumulator.
     */
    @Override
    public void reset() {
        this.setAcc(0);
        this.setImmediate(getId());
        this.setCounter(getImmediate());
    }

    /**
     * Method responsible for performing the accumulation or not.
     *
     * @param data - Value to be used for the computation.
     */
    protected void accumulate(int data) {
        setAcc(getAcc() + data);
        setString(Integer.toString(getId()), Integer.toString(this.getImmediate()));
    }

    /**
     * evaluate(): called by the simulation engine on all events that concern
     * this object. The object is responsible for updating its internal state
     * and for scheduling all pending output events. In this case, it will be
     * checked whether the ports are connected and will execute the compute (int
     * data) method if the R_IN input is high level. It will execute the
     * reset(), tickUp(), and tickDown() methods if their respective entries
     * order it. It will update the output with the ACC value when the
     * computation finishes.
     *
     * @param arg an arbitrary object argument
     */
    @Override
    public void evaluate(Object arg) {

        double time;

        Signal signalClk = null, signalEn = null, signalRst = null, signalDin = null;
        Signal signalDout = null, signalRin = null, signalRout = null, signalDconf = null;

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
        } else if ((signalDconf = getPortDconf().getSignal()) == null) {
            isX = true;
        } else if ((signalDin = getPortDin().getSignal()) == null) {
            isX = true;
        } else if ((signalDout = getPortDout().getSignal()) == null) {
            isX = true;
        } else if ((signalRin = getPortRin().getSignal()) == null) {
            isX = true;
        }

        StdLogic1164 valueRst = getPortRst().getValueOrU();
        StdLogic1164 rOut;

        if (isX || valueRst.is_1()) {
            reset();

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
            StdLogic1164 rIn = getPortRin().getValueOrU();

            if (clk.hasRisingEdge()) {
                if (en.is_1()) {

                    StdLogicVector valueDconf = getPortDconf().getVectorOrUUU();
                    if (!valueDconf.has_UXZ()) {
                        signalDconf = getPortDconf().getSignal();
                        StdLogicVector dConf = (StdLogicVector) signalDconf.getValue();
                        int dImmediate, dId = (int) dConf.getValue();
                        dImmediate = dId >> 8;
                        dId = dId & 0x000000ff;
                        if (dId == getId()) {
                            setImmediate(dImmediate);
                            setCounter(getImmediate());
                            setString(Integer.toString(getId()), Integer.toString(getImmediate()));
                        }
                    }
                    signalDin = getPortDin().getSignal();
                    StdLogicVector dIn = (StdLogicVector) signalDin.getValue();
                    if (getCounter() > 1 && rIn.is_1()) {
                        accumulate((int) dIn.getValue());
                        setCounter(getCounter() - 1);

                        //para portRout
                        if ((signalRout = getPortRout().getSignal()) != null) { // get output
                            rOut = new StdLogic1164(2);
                            time = simulator.getSimTime() + delay;
                            simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout, time, rOut, getPortRout()));
                        }
                    } else {
                        if (getCounter() <= 1 && rIn.is_1()) {
                            accumulate((int) dIn.getValue());
                            StdLogicVector saida = new StdLogicVector(32);
                            saida.setValue(getAcc());			//aqui ocorre a chamada para a computação da saída.
                            vector = saida.copy();
                            time = simulator.getSimTime() + delay;
                            simulator.scheduleEvent(new SimEvent(signalDout, time, vector, getPortDout()));
                            if ((signalRout = getPortRout().getSignal()) != null) { // get output
                                rOut = new StdLogic1164(3);
                                time = simulator.getSimTime() + delay;
                                simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout, time, rOut, getPortRout()));
                            }
                            setCounter(getImmediate());
                            setAcc(0);
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

    /**
     * @return the acc
     */
    public int getAcc() {
        return acc;
    }

    /**
     * @param acc the acc to set
     */
    public void setAcc(int acc) {
        this.acc = acc;
    }

    /**
     * @return the counter
     */
    public int getCounter() {
        return counter;
    }

    /**
     * @param counter the counter to set
     */
    public void setCounter(int counter) {
        this.counter = counter;
    }
}
