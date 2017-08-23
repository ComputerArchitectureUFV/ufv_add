package add.dataflow.sync;

import hades.models.PortStdLogic1164;
import hades.models.PortStdLogicVector;
import hades.models.StdLogic1164;
import hades.models.StdLogicVector;
import hades.signals.Signal;
import hades.signals.SignalStdLogic1164;
import hades.simulator.Port;
import hades.simulator.SimEvent1164;
import hades.symbols.BboxRectangle;
import hades.symbols.BusPortSymbol;
import hades.symbols.Label;
import hades.symbols.PortSymbol;
import hades.symbols.Rectangle;
import hades.symbols.Symbol;
import hades.utils.StringTokenizer;

/**
 * GenericBranch component for the ADD Accelerator Design and Deploy.<br>
 * The component creates the basis for other components with an input and that
 * make a comparison between the inputs.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class GenericBranch extends hades.models.rtlib.GenericRtlibObject {

    private Label stringLabel;
    private Label label_nome;
    private String componentType;
    private String s;
    private PortStdLogic1164 portClk;
    private PortStdLogic1164 portRst;
    private PortStdLogic1164 portRin1;
    private PortStdLogic1164 portRin2;
    private PortStdLogic1164 portEn;
    private PortStdLogicVector portDin1;
    private PortStdLogicVector portDin2;
    private PortStdLogic1164 portIf;
    private PortStdLogic1164 portElse;

    /**
     * Object Constructor.
     */
    public GenericBranch() {
        super();
        setCompName("GEN_CMP");
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
        setPortIf(new PortStdLogic1164(this, "ifout", Port.OUT, null));
        setPortElse(new PortStdLogic1164(this, "elseout", Port.OUT, null));
        setPortDin1(new PortStdLogicVector(this, "din1", Port.IN, null, new Integer(n_bits)));
        setPortDin2(new PortStdLogicVector(this, "din2", Port.IN, null, new Integer(n_bits)));

        ports = new Port[9];
        ports[0] = getPortClk();
        ports[1] = getPortRst();
        ports[2] = getPortEn();
        ports[3] = getPortRin1();
        ports[4] = getPortRin2();
        ports[5] = getPortIf();
        ports[6] = getPortDin1();
        ports[7] = getPortDin2();
        ports[8] = getPortElse();
    }

    /**
     * Method responsible for updating the text displayed by the component.
     *
     * @param s - Text to be updated.
     */
    public void setString(String s) {
        this.setS(s);
        getStringLabel().setText(s);
        getLabel_nome().setText(getName());
        getSymbol().painter.paint(getSymbol(), 100);
    }

    /**
     * Method responsible for updating the component symbol.
     *
     * @param s - Symbol passed automatically.
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
        setString(Integer.toString(1));
        return 1;
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
     *
     * @param l - String to be set to the component name.
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
     * and for scheduling all pending output events.In this case, it will be
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

        Signal signalDin1, signalEn;
        Signal signalDin2, signalIf, signalElse;

        //código para tick_up e Tick_down
        if (getPortClk().getSignal() != null) {
            SignalStdLogic1164 tick = (SignalStdLogic1164) getPortClk().getSignal();
            if (tick.hasRisingEdge()) {
                tickUp();
            } else if (tick.hasFallingEdge()) {
                tickDown();
            }
        }
        //Fim

        boolean isX = false;

        if (getPortClk().getSignal() == null) {
            isX = true;
        } else if (getPortEn().getSignal() == null) {
            isX = true;
        } else if (getPortRst().getSignal() == null) {
            isX = true;
        } else if (getPortRin1().getSignal() == null) {
            isX = true;
        } else if (getPortRin2().getSignal() == null) {
            isX = true;
        } else if (getPortDin1().getSignal() == null) {
            isX = true;
        } else if (getPortDin2().getSignal() == null) {
            isX = true;
        }

        StdLogic1164 valueRst = getPortRst().getValueOrU();
        StdLogic1164 ifOut = new StdLogic1164(2);
        StdLogic1164 elseOut = new StdLogic1164(2);

        if (isX || valueRst.is_1()) {
            reseted();
            //para portIf
            if ((signalIf = getPortIf().getSignal()) != null) { // get output
                time = simulator.getSimTime() + delay;
                simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalIf, time, ifOut, getPortIf()));
            }

            //para portElse
            if ((signalElse = getPortElse().getSignal()) != null) { // get output
                time = simulator.getSimTime() + delay;
                simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalElse, time, elseOut, getPortElse()));
            }
            return;
        }
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
                    int result = (compute((int) dIn1.getValue(), (int) dIn2.getValue()));

                    if (result == 0) {
                        elseOut = new StdLogic1164(3);
                    } else if (result == 1) {
                        ifOut = new StdLogic1164(3);
                    }
                }

                //para portIf
                if ((signalIf = getPortIf().getSignal()) != null) { // get output
                    time = simulator.getSimTime() + delay;
                    simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalIf, time, ifOut, getPortIf()));
                }

                //para portElse
                if ((signalElse = getPortElse().getSignal()) != null) { // get output
                    time = simulator.getSimTime() + delay;
                    simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalElse, time, elseOut, getPortElse()));
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
     * @return - TRUE means that the symbol will be built dynamically.
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
        BusPortSymbol busportsymbol;

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
        portsymbol.initialize("1800 600 " + getPortIf().getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1800 1200 " + getPortElse().getName());
        symbol.addMember(portsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 1200 D_IN1");
        symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 2400 D_IN2");
        symbol.addMember(busportsymbol);

        setLabel_nome(new Label());
        getLabel_nome().initialize("0 -600 " + getName());
        symbol.addMember(getLabel_nome());

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
     * @return the label_nome
     */
    public Label getLabel_nome() {
        return label_nome;
    }

    /**
     * @param label_nome the label_nome to set
     */
    public void setLabel_nome(Label label_nome) {
        this.label_nome = label_nome;
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
     * @return the portIf
     */
    public PortStdLogic1164 getPortIf() {
        return portIf;
    }

    /**
     * @param portIf the portIf to set
     */
    public void setPortIf(PortStdLogic1164 portIf) {
        this.portIf = portIf;
    }

    /**
     * @return the portElse
     */
    public PortStdLogic1164 getPortElse() {
        return portElse;
    }

    /**
     * @param portElse the portElse to set
     */
    public void setPortElse(PortStdLogic1164 portElse) {
        this.portElse = portElse;
    }
}
