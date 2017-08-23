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
 * GenericI component for the ADD Accelerator Design and Deploy.<br>
 * The component creates the basis for other components with an input and that
 * perform the computation with an (immediate) id.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class GenericI extends hades.models.rtlib.GenericRtlibObject {

    private Label stringLabelId;

    private Label stringLabelImmediate;

    private Label labelNome;
    private String componentId;
    private String componentImmediate;
    private String componentType;
    private Rectangle background;
    private PortStdLogic1164 portClk;
    private PortStdLogic1164 portRst;
    private PortStdLogic1164 portRin;
    private PortStdLogic1164 portRout;
    private PortStdLogic1164 portEn;
    private PortStdLogicVector portDin;
    private PortStdLogicVector portDout;
    private PortStdLogicVector portDconf;

    //32 bits for configuration where the least significant 24 is the VALUE 
    //and the 8 most significant is the device ID.
    private int id;
    private int immediate;

    /**
     * Object Constructor.
     */
    public GenericI() {
        super();
        setCompName("GEN_I");
        id = 1;
        immediate = id;
    }

    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    @Override
    public void constructPorts() {
        setPortClk(new PortStdLogic1164(this, "clk", Port.IN, null));
        setPortRin(new PortStdLogic1164(this, "rin", Port.IN, null));
        setPortRout(new PortStdLogic1164(this, "rout", Port.OUT, null));
        setPortEn(new PortStdLogic1164(this, "en", Port.IN, null));
        setPortRst(new PortStdLogic1164(this, "rst", Port.IN, null));
        setPortDconf(new PortStdLogicVector(this, "dconf", Port.IN, null, new Integer(32)));
        setPortDin(new PortStdLogicVector(this, "din", Port.IN, null, new Integer(n_bits)));
        setPortDout(new PortStdLogicVector(this, "dout", Port.OUT, null, new Integer(n_bits)));

        ports = new Port[8];
        ports[0] = getPortClk();
        ports[1] = getPortDin();
        ports[2] = getPortDout();
        ports[3] = getPortRst();
        ports[4] = getPortRin();
        ports[5] = getPortRout();
        ports[6] = getPortDconf();
        ports[7] = getPortEn();
    }

    /**
     * Method responsible for updating the text displayed by the component.
     *
     * @param componentId - Text to be updated.
     * @param componentImmediate - Text to be updated.
     */
    public void setString(String componentId, String componentImmediate) {
        this.setComponentId(componentId);
        this.setComponentImmediate(componentImmediate);

        getStringLabelId().setText("ID=" + componentId);
        getStringLabelImmediate().setText("IM=" + componentImmediate);

        getLabelNome().setText(getName());
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
     * Method responsible for the computation of the output and set the new text
     * to be shown by the component. In this case the id.
     *
     * @param data - Value to be used for the computation.
     * @return - Return of computation
     */
    public int compute(int data) {
        setString(Integer.toString(getId()), Integer.toString(getImmediate()));
        return data;
    }

    /**
     * Method executed when computing is not performed.
     */
    public void notCompute() {
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level. It sets the new text to be shown by the component. In this case
     * the id.
     */
    public void reset() {
        setString(Integer.toString(getId()), Integer.toString(getImmediate()));
        setImmediate(getId());
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
     * @param l - String to be set in component name.
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
     * data) method if the R_IN input is high level. It will execute the
     * reset(), tickUp(), and tickDown() methods if their respective entries
     * order it. It will update the output with the compute(int data) method
     * result.
     *
     * @param arg an arbitrary object argument
     */
    @Override
    public void evaluate(Object arg) {

        double time;

        Signal signalClk = null, signalRst = null, signalDin = null, signalDout = null, signalEn = null;
        Signal signalRin = null, signalRout = null, signalDconf = null;

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
                        StdLogicVector conf = (StdLogicVector) signalDconf.getValue();
                        int dImmediate, dId = (int) conf.getValue();
                        dImmediate = dId >> 8;
                        dId = dId & 0x000000ff;
                        if (dId == getId()) {
                            setImmediate(dImmediate);
                            setString(Integer.toString(getId()), Integer.toString(getImmediate()));
                        }
                    }

                    signalDin = getPortDin().getSignal();
                    StdLogicVector dIn = (StdLogicVector) signalDin.getValue();
                    if (rIn.is_1()) {
                        StdLogicVector saida = new StdLogicVector(32);
                        saida.setValue(compute((int) dIn.getValue()));			//aqui ocorre a chamada para a computação da saída.
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
     * @return - TRUE means that the symbol will be made dynamically.
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
        portsymbol.initialize("1200 1800 " + getPortClk().getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("600 1800 " + getPortRst().getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("0 600 " + getPortRin().getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1800 600 " + getPortRout().getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("600 0 " + getPortEn().getName());
        symbol.addMember(portsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 1200 " + getPortDin().getName());
        symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("1800 1200 " + getPortDout().getName());
        symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 1800 " + getPortDconf().getName());
        symbol.addMember(busportsymbol);

        setLabelNome(new Label());
        getLabelNome().initialize("0 -1000 " + getName());
        symbol.addMember(getLabelNome());

        Label label = new Label();
        label.initialize("900 600 2 " + getComponentType());
        symbol.addMember(label);

        setStringLabelId(new Label());
        getStringLabelId().initialize("0 -600 ID=" + getComponentId());
        symbol.addMember(getStringLabelId());

        setStringLabelImmediate(new Label());
        getStringLabelImmediate().initialize("0 -200 IM=" + getComponentImmediate());
        symbol.addMember(getStringLabelImmediate());

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
                + " " + getId()
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
                setId(0);
                constructStandardValues();
                constructPorts();
            } else if (n_tokens == 1) {
                versionId = Integer.parseInt(st.nextToken());
                n_bits = 16;
                setId(0);
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
                setId(Short.parseShort(st.nextToken()));
                constructStandardValues();
                constructPorts();
            } else if (n_tokens == 4 || n_tokens == 5) {
                versionId = Integer.parseInt(st.nextToken());
                n_bits = Integer.parseInt(st.nextToken());
                setId(Short.parseShort(st.nextToken()));
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
     * Method responsible for changing the value of the id for more or less,
     * depending on whether the mouse click is done by the right or left button
     * respectively.
     *
     * @param me - Object where the event occurred.
     */
    @Override
    public void mousePressed(java.awt.event.MouseEvent me) {

        if (me.isShiftDown()) { // decrement
            if (getId() > 1) {
                setId((int) (getId() - 1));
            }
        } else { // increment
            setId((int) (getId() + 1));
        }
        setString(Integer.toString(getId()), Integer.toString(getImmediate()));
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the immediate
     */
    public int getImmediate() {
        return immediate;
    }

    /**
     * @param immediate the immediate to set
     */
    public void setImmediate(int immediate) {
        this.immediate = immediate;
    }

    /**
     * @return the stringLabelId
     */
    public Label getStringLabelId() {
        return stringLabelId;
    }

    /**
     * @param stringLabelId the stringLabelId to set
     */
    public void setStringLabelId(Label stringLabelId) {
        this.stringLabelId = stringLabelId;
    }

    /**
     * @return the stringLabelImmediate
     */
    public Label getStringLabelImmediate() {
        return stringLabelImmediate;
    }

    /**
     * @param stringLabelImmediate the stringLabelImmediate to set
     */
    public void setStringLabelImmediate(Label stringLabelImmediate) {
        this.stringLabelImmediate = stringLabelImmediate;
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
     * @return the componentId
     */
    public String getComponentId() {
        return componentId;
    }

    /**
     * @param componentId the componentId to set
     */
    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    /**
     * @return the componentImmediate
     */
    public String getComponentImmediate() {
        return componentImmediate;
    }

    /**
     * @param componentImmediate the componentImmediate to set
     */
    public void setComponentImmediate(String componentImmediate) {
        this.componentImmediate = componentImmediate;
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
     * @return the background
     */
    public Rectangle getBackground() {
        return background;
    }

    /**
     * @param background the background to set
     */
    public void setBackground(Rectangle background) {
        this.background = background;
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
     * @return the portRin
     */
    public PortStdLogic1164 getPortRin() {
        return portRin;
    }

    /**
     * @param portRin the portRin to set
     */
    public void setPortRin(PortStdLogic1164 portRin) {
        this.portRin = portRin;
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
     * @return the portDin
     */
    public PortStdLogicVector getPortDin() {
        return portDin;
    }

    /**
     * @param portDin the portDin to set
     */
    public void setPortDin(PortStdLogicVector portDin) {
        this.portDin = portDin;
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

    /**
     * @return the portDconf
     */
    public PortStdLogicVector getPortDconf() {
        return portDconf;
    }

    /**
     * @param portDconf the portDconf to set
     */
    public void setPortDconf(PortStdLogicVector portDconf) {
        this.portDconf = portDconf;
    }
}
