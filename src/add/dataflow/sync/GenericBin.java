package add.dataflow.sync;

import hades.models.PortStdLogic1164;
import hades.models.PortStdLogicVector;
import hades.models.StdLogic1164;
import hades.models.StdLogicVector;
import hades.signals.Signal;
import hades.signals.SignalStdLogic1164;
import hades.simulator.Port;
import hades.simulator.SimEvent;
import hades.simulator.SimEvent1164;
import hades.symbols.BboxRectangle;
import hades.symbols.BusPortSymbol;
import hades.symbols.Label;
import hades.symbols.PortSymbol;
import hades.symbols.Rectangle;
import hades.symbols.Symbol;
import hades.utils.StringTokenizer;

/**
 * GenericBin component for the ADD Accelerator Design and Deploy.<br>
 * The component creates the basis for other components with two inputs.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class GenericBin extends hades.models.rtlib.GenericRtlibObject {

    private Label stringLabel;
    private Label labelNome;
    private String componentType;
    private String s;
    private PortStdLogic1164 portClk;
    private PortStdLogic1164 portRst;
    private PortStdLogic1164 portRin1;
    private PortStdLogic1164 portRin2;
    private PortStdLogic1164 portRout;
    private PortStdLogic1164 portEn;
    private PortStdLogicVector portDin1;
    private PortStdLogicVector portDin2;
    private PortStdLogicVector portDout;

    /**
     * Object Constructor.
     */
    public GenericBin() {
        super();
        setCompName("GEN_BIN");
    }

    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    @Override
    public void constructPorts() {
        setPortClk(new PortStdLogic1164(this, "clk", Port.IN, null));
        setPortRst(new PortStdLogic1164(this, "rst", Port.IN, null));
        setPortEn(new PortStdLogic1164(this, "en", Port.IN, null));
        setPortRin1(new PortStdLogic1164(this, "rin1", Port.IN, null));
        setPortRin2(new PortStdLogic1164(this, "rin2", Port.IN, null));
        setPortRout(new PortStdLogic1164(this, "rout", Port.IN, null));
        setPortDin1(new PortStdLogicVector(this, "din1", Port.IN, null, new Integer(n_bits)));
        setPortDin2(new PortStdLogicVector(this, "din2", Port.IN, null, new Integer(n_bits)));
        setPortDout(new PortStdLogicVector(this, "dout", Port.OUT, null, new Integer(n_bits)));

        ports = new Port[9];
        ports[0] = getPortClk();
        ports[1] = getPortRst();
        ports[2] = getPortEn();
        ports[3] = getPortRin1();
        ports[4] = getPortRin2();
        ports[5] = getPortRout();
        ports[6] = getPortDin1();
        ports[7] = getPortDin2();
        ports[8] = getPortDout();
    }

    /**
     * Method responsible for updating the text displayed by the component.
     *
     * @param s - Text to be updated.
     */
    public void setString(String s) {
        this.setS(s);
        getStringLabel().setText(s);
        getLabelNome().setText(getName());
        getSymbol().painter.paint(getSymbol(), 100);
    }

    /**
     * Method responsible for updating the component symbol.
     *
     * @param s
     */
    @Override
    public void setSymbol(Symbol s) {
        symbol = s;
    }

    /**
     * Method responsible for the computation of the output.
     *
     * @param data1 - Value to be used for the computation related to input 1.
     * @param data2 - Value to be used for the computation related to input 1.
     * @return - Return of computation
     */
    public int compute(int data1, int data2) {
        setString(Integer.toString(data1));
        return data1;
    }

    /**
     * Method executed when computing is not performed. In this case it clears
     * the text displayed by the component.
     */
    public void notCompute() {
        setString("NULL");
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level.In this case it clears the text displayed by the component.
     */
    public void reseted() {
        setS("NULL");
        setString(getS());
    }

    /**
     * Method executed when the clock signal goes to high logic level.
     */
    public void tickUp() {

    }

    /**
     * Method executed when the clock signal goes to low logic level.
     */
    public void tickDown() {

    }

    /**
     * Method responsible for changing the label that displays the name of the
     * component.
     */
    public void setCompName(String l) {
        if (l.equals("")) {
            this.setComponentType(".");
        } else {
            this.setComponentType(l);
        }
    }

    /**
     * evaluate(): called by the simulation engine on all events that concern
     * this object. The object is responsible for updating its internal state
     * and for scheduling all pending output events. In this case, it will be
     * checked whether the ports are connected and will execute the compute (int
     * data) method if the R_IN (1 and 2) inputs are high level. It will execute
     * the reseted(), tickUp(), and tickDown() methods if their respective
     * entries order it. It will update the output with the compute(int data)
     * method result.
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

        StdLogic1164 value_RST = getPortRst().getValueOrU();
        StdLogic1164 rOut;

        if (isX || value_RST.is_1()) {
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

            if (clk.hasRisingEdge()) {
                if (en.is_1()) {

                    signalDin1 = getPortDin1().getSignal();
                    StdLogicVector dIn1 = (StdLogicVector) signalDin1.getValue();

                    signalDin2 = getPortDin2().getSignal();
                    StdLogicVector dIn2 = (StdLogicVector) signalDin2.getValue();

                    if ((rIn1.is_1()) && (rIn2.is_1())) {
                        StdLogicVector saida = new StdLogicVector(32);
                        saida.setValue(compute((int) dIn1.getValue(), (int) dIn2.getValue()));//aqui ocorre a chamada para a computação da saída.
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

    /**
     * Method responsible for indicating to the simulator that the component's
     * symbol will be constructed dynamically by the constructDynamicSymbol()
     * method, or will be read from a file of the same name as the ".sym"
     * extension.
     *
     * @return - - TRUE means that the symbol will be built dynamically.
     */
    @Override
    public boolean needsDynamicSymbol() {
        return true;
    }

    /**
     * Method responsible for dynamically constructing the component symbol.
     */
    @Override
    public void constructDynamicSymbol() {
        symbol = new Symbol();
        symbol.setParent(this);

        BboxRectangle bbr = new BboxRectangle();
        bbr.initialize("-200 -900 2000 3200");
        symbol.addMember(bbr);
        Rectangle rec = new Rectangle();
        rec.initialize("0 0 1800 3000");
        symbol.addMember(rec);

        PortSymbol portsymbol;

        portsymbol = new PortSymbol();
        portsymbol.initialize("1200 3000 " + getPortClk().getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("600 3000 " + getPortRst().getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("600 0 " + getPortEn().getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("0 600 " + getPortRin1().getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("0 1800 " + getPortRin2().getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1800 600 " + getPortRout().getName());
        symbol.addMember(portsymbol);

        BusPortSymbol busportsymbol;

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 1200 " + getPortDin1().getName());
        symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 2400 " + getPortDin2().getName());
        symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("1800 1200 " + getPortDout().getName());
        symbol.addMember(busportsymbol);

        setLabelNome(new Label());
        getLabelNome().initialize("0 -600 " + getName());
        symbol.addMember(getLabelNome());

        Label label0 = new Label();
        label0.initialize("900 600 2 " + getComponentType());
        symbol.addMember(label0);

        setStringLabel(new Label());
        getStringLabel().initialize("0 -200 " + getS());

        symbol.addMember(getStringLabel());

    }

    /**
     * Method responsible for writing component settings to the file saved by
     * the simulator.
     *
     * @param ps -Simulator writing object.
     */
    @Override
    public void write(java.io.PrintWriter ps) {
        ps.print(" " + versionId
                + " " + n_bits
                + " " + delay
                + " b");
    }

    /**
     * Method responsible for reading the component settings in the file saved
     * by the simulator.
     *
     * @param s - Settings for the component read from the file saved by the
     * simulator.
     * @return - Returns true if the settings are read successfully.
     */
    @Override
    public boolean initialize(String s) {
        StringTokenizer st = new StringTokenizer(s);
        int n_tokens = st.countTokens();
        try {
            if (n_tokens == 0) {
                versionId = 1001;
                n_bits = 16;
                constructStandardValues();
                constructPorts();
            } else if (n_tokens == 1) {
                versionId = Integer.parseInt(st.nextToken());
                n_bits = 16;
                constructStandardValues();
                constructPorts();
            } else if (n_tokens == 2) {
                versionId = Integer.parseInt(st.nextToken());
                n_bits = Integer.parseInt(st.nextToken());
                constructStandardValues();
                constructPorts();
            } else if (n_tokens == 3 || n_tokens == 4) {
                versionId = Integer.parseInt(st.nextToken());
                n_bits = Integer.parseInt(st.nextToken());
                constructStandardValues();
                constructPorts();
                setDelay(st.nextToken());
            } else {
                throw new Exception("invalid number of arguments");
            }
        } catch (Exception e) {
            message("-E- " + toString() + ".initialize(): " + e + " " + s);
            e.printStackTrace();
        }
        return true;
    }

    /**
     * @return the stringLabel
     */
    public Label getStringLabel() {
        return stringLabel;
    }

    /**
     * @param stringLabel the stringLabel to set
     */
    public void setStringLabel(Label stringLabel) {
        this.stringLabel = stringLabel;
    }

    /**
     * @return the labelNome
     */
    public Label getLabelNome() {
        return labelNome;
    }

    /**
     * @param labelNome the labelNome to set
     */
    public void setLabelNome(Label labelNome) {
        this.labelNome = labelNome;
    }

    /**
     * @return the componentType
     */
    public String getComponentType() {
        return componentType;
    }

    /**
     * @param componentType the componentType to set
     */
    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    /**
     * @return the s
     */
    public String getS() {
        return s;
    }

    /**
     * @param s the s to set
     */
    public void setS(String s) {
        this.s = s;
    }

    /**
     * @return the portClk
     */
    public PortStdLogic1164 getPortClk() {
        return portClk;
    }

    /**
     * @param portClk the portClk to set
     */
    public void setPortClk(PortStdLogic1164 portClk) {
        this.portClk = portClk;
    }

    /**
     * @return the portRst
     */
    public PortStdLogic1164 getPortRst() {
        return portRst;
    }

    /**
     * @param portRst the portRst to set
     */
    public void setPortRst(PortStdLogic1164 portRst) {
        this.portRst = portRst;
    }

    /**
     * @return the portRin1
     */
    public PortStdLogic1164 getPortRin1() {
        return portRin1;
    }

    /**
     * @param portRin1 the portRin1 to set
     */
    public void setPortRin1(PortStdLogic1164 portRin1) {
        this.portRin1 = portRin1;
    }

    /**
     * @return the portRin2
     */
    public PortStdLogic1164 getPortRin2() {
        return portRin2;
    }

    /**
     * @param portRin2 the portRin2 to set
     */
    public void setPortRin2(PortStdLogic1164 portRin2) {
        this.portRin2 = portRin2;
    }

    /**
     * @return the portRout
     */
    public PortStdLogic1164 getPortRout() {
        return portRout;
    }

    /**
     * @param portRout the portRout to set
     */
    public void setPortRout(PortStdLogic1164 portRout) {
        this.portRout = portRout;
    }

    /**
     * @return the portEn
     */
    public PortStdLogic1164 getPortEn() {
        return portEn;
    }

    /**
     * @param portEn the portEn to set
     */
    public void setPortEn(PortStdLogic1164 portEn) {
        this.portEn = portEn;
    }

    /**
     * @return the portDin1
     */
    public PortStdLogicVector getPortDin1() {
        return portDin1;
    }

    /**
     * @param portDin1 the portDin1 to set
     */
    public void setPortDin1(PortStdLogicVector portDin1) {
        this.portDin1 = portDin1;
    }

    /**
     * @return the portDin2
     */
    public PortStdLogicVector getPortDin2() {
        return portDin2;
    }

    /**
     * @param portDin2 the portDin2 to set
     */
    public void setPortDin2(PortStdLogicVector portDin2) {
        this.portDin2 = portDin2;
    }

    /**
     * @return the portDout
     */
    public PortStdLogicVector getPortDout() {
        return portDout;
    }

    /**
     * @param portDout the portDout to set
     */
    public void setPortDout(PortStdLogicVector portDout) {
        this.portDout = portDout;
    }
}
