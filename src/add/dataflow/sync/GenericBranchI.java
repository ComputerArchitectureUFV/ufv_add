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
 * GenericBranchI component for the ADD Accelerator Design and Deploy.<br>
 * The component creates the basis for other components with an input and that
 * make a comparison with a (immediate) constant.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class GenericBranchI extends hades.models.rtlib.GenericRtlibObject {

    protected Label stringLabelId, stringLabelImmediate, labelNome;
    protected String componentId, componentImmediate, componentType;
    protected Rectangle background;

    protected PortStdLogic1164 portClk, portRst, portRin, portIf, portElse, portEn;
    protected PortStdLogicVector portDin, portDconf;

    //32 bits for configuration where the least significant 24 is the VALUE 
    //and the 8 most significant is the device ID.
    protected int id;
    protected int immediate;

    /**
     * Object Constructor.
     */
    public GenericBranchI() {
        super();
        setCompName("G_COMPI");
        id = 0;
        immediate = id;
    }

    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    @Override
    public void constructPorts() {
        portClk = new PortStdLogic1164(this, "clk", Port.IN, null);
        portRst = new PortStdLogic1164(this, "rst", Port.IN, null);
        portEn = new PortStdLogic1164(this, "en", Port.IN, null);
        portRin = new PortStdLogic1164(this, "rin", Port.IN, null);
        portIf = new PortStdLogic1164(this, "ifout", Port.OUT, null);
        portElse = new PortStdLogic1164(this, "elseout", Port.OUT, null);
        portDin = new PortStdLogicVector(this, "din", Port.IN, null, new Integer(n_bits));
        portDconf = new PortStdLogicVector(this, "dconf", Port.IN, null, new Integer(32));

        ports = new Port[8];
        ports[0] = portClk;
        ports[1] = portRst;
        ports[2] = portEn;
        ports[3] = portRin;
        ports[4] = portIf;
        ports[5] = portElse;
        ports[6] = portDin;
        ports[7] = portDconf;
    }

    /**
     * Method responsible for updating the text displayed by the component.
     *
     * @param componentId - Text to be updated.
     */
    public void setString(String componentId, String componentImmediate) {
        this.componentId = componentId;
        this.componentImmediate = componentImmediate;

        stringLabelId.setText("ID=" + componentId);
        stringLabelImmediate.setText("IM=" + componentImmediate);

        labelNome.setText(getName());
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
     * @param data - Value to be used for the computation.
     * @return - Return of computation
     */
    public int compute(int data) {
        setString(Integer.toString(id), Integer.toString(immediate));
        return 1;
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level.In this case it clears the text displayed by the component.
     */
    public void reseted() {
        setString(Integer.toString(id), Integer.toString(immediate));
        immediate = id;
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
            this.componentType = ".";
        } else {
            this.componentType = l;
        }
    }

    /**
     * evaluate(): called by the simulation engine on all events that concern
     * this object. The object is responsible for updating its internal state
     * and for scheduling all pending output events. In this case, it will be
     * checked whether the ports are connected and will execute the compute (int
     * data) method if the R_IN input is high level. It will execute the
     * reseted(), tickUp(), and tickDown() methods if their respective entries
     * order it. It will update the output with the compute(int data) method
     * result.
     *
     * @param arg an arbitrary object argument
     */
    @Override
    public void evaluate(Object arg) {

        double time;

        Signal signalDin, signalIf, signalElse, signalConf = null;

        //código para tick_up e Tick_down
        if (portClk.getSignal() != null) {
            SignalStdLogic1164 tick = (SignalStdLogic1164) portClk.getSignal();
            if (tick.hasRisingEdge()) {
                tickUp();
            } else if (tick.hasFallingEdge()) {
                tickDown();
            }
        }
        //Fim

        boolean isX = false;

        if ((portClk.getSignal()) == null) {
            isX = true;
        } else if (portRst.getSignal() == null) {
            isX = true;
        } else if (portEn.getSignal() == null) {
            isX = true;
        } else if (portDconf.getSignal() == null) {
            isX = true;
        } else if (portDin.getSignal() == null) {
            isX = true;
        } else if (portRin.getSignal() == null) {
            isX = true;
        }

        StdLogic1164 valueRst = portRst.getValueOrU();
        StdLogic1164 ifOut = new StdLogic1164(2);
        StdLogic1164 elseOut = new StdLogic1164(2);

        if (isX || valueRst.is_1()) {
            reseted();

            //para portIf
            if ((signalIf = portIf.getSignal()) != null) { // get output
                time = simulator.getSimTime() + delay;
                simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalIf, time, ifOut, portIf));
            }
            //para portElse
            if ((signalElse = portElse.getSignal()) != null) { // get output
                elseOut = new StdLogic1164(2);
                time = simulator.getSimTime() + delay;
                simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalElse, time, elseOut, portElse));
            }
            return;
        }

        SignalStdLogic1164 clk = (SignalStdLogic1164) portClk.getSignal();
        StdLogic1164 en = portEn.getValueOrU();
        StdLogic1164 rIn = portRin.getValueOrU();

        if (clk.hasRisingEdge()) {
            if (en.is_1()) {

                StdLogicVector valueDconf = portDconf.getVectorOrUUU();
                if (!valueDconf.has_UXZ()) {
                    signalConf = portDconf.getSignal();
                    StdLogicVector conf = (StdLogicVector) signalConf.getValue();
                    int dImmediate, dId = (int) conf.getValue();
                    dImmediate = dId >> 8;
                    dId = dId & 0x000000ff;
                    if (dId == id) {
                        immediate = dImmediate;
                        setString(Integer.toString(id), Integer.toString(immediate));
                    }
                }
                signalDin = portDin.getSignal();
                StdLogicVector d_in = (StdLogicVector) signalDin.getValue();

                if (rIn.is_1()) {

                    int result = (compute((int) d_in.getValue()));

                    if (result == 0) {
                        elseOut = new StdLogic1164(3);
                    } else if (result == 1) {
                        ifOut = new StdLogic1164(3);
                    }

                }

                //para portIf
                if ((signalIf = portIf.getSignal()) != null) { // get output
                    time = simulator.getSimTime() + delay;
                    simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalIf, time, ifOut, portIf));
                }

                //para portElse
                if ((signalElse = portElse.getSignal()) != null) { // get output
                    time = simulator.getSimTime() + delay;
                    simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalElse, time, elseOut, portElse));
                }
            }
        }
    }

    /**
     * Method responsible for indicating to the simulator that the
     * component'componentId symbol will be constructed dynamically by the
     * constructDynamicSymbol() method, or will be read from a file of the same
     * name as the ".sym" extension.
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
        bbr.initialize("-200 -1300 2000 2000");
        symbol.addMember(bbr);

        Rectangle rec = new Rectangle();
        rec.initialize("0 0 1800 1800");
        symbol.addMember(rec);

        PortSymbol portsymbol;
        BusPortSymbol busportsymbol;

        portsymbol = new PortSymbol();
        portsymbol.initialize("1200 1800 " + portClk.getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("600 1800 " + portRst.getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("0 600 " + portRin.getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1800 600 " + portIf.getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1800 1200 " + portElse.getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("600 0 " + portEn.getName());
        symbol.addMember(portsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 1200 " + portDin.getName());
        symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 1800 " + portDconf.getName());
        symbol.addMember(busportsymbol);

        labelNome = new Label();
        labelNome.initialize("0 -1000 " + getName());
        symbol.addMember(labelNome);

        Label label0 = new Label();
        label0.initialize("900 600 2 " + componentType);
        symbol.addMember(label0);

        stringLabelId = new Label();
        stringLabelId.initialize("0 -600 ID=" + componentId);
        symbol.addMember(stringLabelId);

        stringLabelImmediate = new Label();
        stringLabelImmediate.initialize("0 -200 IM=" + componentImmediate);
        symbol.addMember(stringLabelImmediate);
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
                + " " + id
                + " " + delay
                + " i");
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
                id = 0;
                constructStandardValues();
                constructPorts();
            } else if (n_tokens == 1) {
                versionId = Integer.parseInt(st.nextToken());
                n_bits = 16;
                id = 0;
                constructStandardValues();
                constructPorts();
            } else if (n_tokens == 2) {
                versionId = Integer.parseInt(st.nextToken());
                n_bits = Integer.parseInt(st.nextToken());
                constructStandardValues();
                constructPorts();
            } else if (n_tokens == 3) {
                versionId = Integer.parseInt(st.nextToken());
                n_bits = Integer.parseInt(st.nextToken());
                id = Integer.parseInt(st.nextToken());
                constructStandardValues();
                constructPorts();
            } else if (n_tokens == 4 || n_tokens == 5) {
                versionId = Integer.parseInt(st.nextToken());
                n_bits = Integer.parseInt(st.nextToken());
                id = Integer.parseInt(st.nextToken());
                constructStandardValues();
                constructPorts();
                setDelay(st.nextToken());
            } else {
                throw new Exception("invalid number of arguments");
            }
        } catch (Exception e) {
            message("-E- " + toString() + ".initialize(): " + e + " " + s);
        }
        return true;
    }

    /**
     * Method responsible for changing the value of the constant for more or
     * less, depending on whether the mouse click is done by the right or left
     * button respectively.
     *
     * @param me - Object where the event occurred.
     */
    @Override
    public void mousePressed(java.awt.event.MouseEvent me) {

        if (me.isShiftDown()) { // decrement
            if (id >= 0) {
                id = id - 1;
            }
        } else { // increment
            id = id + 1;
        }
        setString(Integer.toString(id), Integer.toString(immediate));
    }
}
