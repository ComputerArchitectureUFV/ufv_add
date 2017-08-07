import math

from veriloggen import *


def make_component_in(name, num_ports, data_width, data_width_ext):
    m = Module(name)
    ports = {}
    clk = m.Input('clk')
    rst = m.Input('rst')
    start = m.Input('start')
    num_data = m.Input('num_data', 32)
    num_conf = m.Input('num_conf', 32)
    rdy = m.Input('rdy')
    available_read = m.Input('available_read')
    rd_data = m.Input('rd_data', data_width_ext)
    req_rd_data = m.OutputReg('req_rd_data')
    dconf = m.OutputReg('dconf', 32)
    en = m.Output('en')

    for i in range(num_ports):
        ports['rout%d' % (i + 1)] = m.Output('rout%d' % (i + 1))
    for i in range(num_ports):
        ports['dout%d' % (i + 1)] = m.Output('dout%d' % (i + 1), data_width)

    bits = int(math.ceil(math.log((data_width_ext // (data_width * num_ports)), 2)))
    if bits == 0:
        bits = 2
    else:
        bits = bits + 1

    index_conf = m.Reg('index_conf', 4)
    reg_en = m.Reg('reg_en')
    index_data = m.Reg('index_data', bits)
    data = m.Reg('data', data_width_ext)
    data_out = m.Reg('data_out', num_ports * data_width)
    r_out = m.Reg('r_out')
    cont_data = m.Reg('cont_data', 32)
    cont_conf = m.Reg('cont_conf', 32)
    fsm_cs = m.Reg('fms_cs', 3)
    flag_cpy_data = m.Reg('flag_cpy_data')
    wdata = m.Wire('wdata', (data_width * num_ports), data_width_ext // (data_width * num_ports))
    wconf = m.Wire('wconf', 32, data_width_ext // (32))
    i = m.Genvar('i')
    m.GenerateFor(i(0), i < (data_width_ext // (data_width * num_ports)), i.inc(), 'gen_1').Assign(
        wdata[i](data[i * (data_width * num_ports):((i * (data_width * num_ports)) + (data_width * num_ports))])
    )

    i = m.Genvar('j')
    m.GenerateFor(i(0), i < 16, i.inc(), 'gen_2').Assign(
        wconf[i](data[i * (32):((i * (32)) + (32))])
    )

    for i in range(num_ports):
        ports['rout%d' % (i + 1)].assign(r_out)
        ports['dout%d' % (i + 1)].assign(data_out[i * data_width:i * data_width + data_width])

    en.assign(rdy & reg_en)

    FSM_WAIT = m.Localparam('FSM_WAIT', 0, 3)
    FSM_RD_DATA = m.Localparam('FSM_RD_DATA', 1, 3)
    FSM_DONE = m.Localparam('FSM_DONE', 2, 3)

    m.Always(Posedge(clk))(
        If(rst)(
            req_rd_data(Int(0, 1, 2)),
            dconf(Int(0, dconf.width, 10)),
            index_conf(Int(0, 4, 10)),  
            index_data(Int(0, index_data.width, 10)),
            data(Int(0, data.width, 10)),
            data_out(Int(0, data_out.width, 10)),
            r_out(Int(0, 1, 2)),
            cont_data(Int(0, cont_data.width, 10)),
            cont_conf(Int(0, cont_conf.width, 10)),
            reg_en(Int(0, 1, 2)),
            flag_cpy_data(Int(0, 1, 2)),
            fsm_cs(FSM_WAIT),
        ).Elif(start)(
            req_rd_data(Int(0, 1, 2)),
            reg_en(Int(0, 1, 2)),
            r_out(Int(0, 1, 2)),
            Case(fsm_cs)(
                When(FSM_WAIT)(
                    If(available_read)(
                        req_rd_data(Int(1, 1, 2)),
                        flag_cpy_data(Int(0, 1, 2)),
                        fsm_cs(FSM_RD_DATA)
                    ).Elif(cont_data >= (num_data / num_ports))(
                        fsm_cs(FSM_DONE),
                        reg_en(Int(1, 1, 2))
                    ).Else(
                        fsm_cs(FSM_WAIT)
                    )
                ),
                When(FSM_RD_DATA)(
                    If(cont_conf < num_conf)(  
                        If(index_conf < (data_width_ext // (32)) - 1)(
                            If(flag_cpy_data == Int(0, 1, 2))(
                                data(rd_data),
                                flag_cpy_data(Int(1, 1, 2)),
                                dconf(rd_data[0:32]), 
                                cont_conf.inc(),
                                index_conf.inc(),
                            ).Else(
                                dconf(wconf[index_conf]),  
                                cont_conf.inc(),
                                index_conf.inc(),
                            ),
                            fsm_cs(FSM_RD_DATA)
                        ).Else(
                            dconf(wconf[index_conf]),
                            cont_conf.inc(),
                            index_conf(Int(0, 4, 10)),
                            If(available_read)(
                                req_rd_data(Int(1, 1, 2)),
                                flag_cpy_data(Int(0, 1, 2)),
                                fsm_cs(FSM_RD_DATA)
                            ).Else(
                                fsm_cs(FSM_WAIT)
                            )
                        )
                    ).Elif(cont_data < (num_data / num_ports))(
                        If(index_data < (data_width_ext // (data_width * num_ports)) - 1)(
                            If(flag_cpy_data == Int(0, 1, 2))(
                                data(rd_data),
                                data_out(rd_data[0:(data_width * num_ports)]),
                                flag_cpy_data(Int(1, 1, 2))
                            ).Else(
                                data_out(wdata[index_data]),
                            ),
                            r_out(Int(1, 1, 2)),
                            If(rdy)(
                                reg_en(Int(1, 1, 2)),
                                cont_data.inc(),
                                index_data.inc()
                            ),
                            fsm_cs(FSM_RD_DATA)
                        ).Else(
                            data_out(wdata[index_data]),
                            r_out(Int(1, 1, 2)),
                            If(rdy)(
                                reg_en(Int(1, 1, 2)),
                                index_data(Int(0, index_data.width, 10)),
                                cont_data.inc(),
                                If(available_read)(
                                    req_rd_data(Int(1, 1, 2)),
                                    flag_cpy_data(Int(0, 1, 2)),
                                    fsm_cs(FSM_RD_DATA)
                                ).Else(
                                    fsm_cs(FSM_WAIT)
                                )
                            )
                        )
                    ).Else(
                        reg_en(Int(1, 1, 2)),
                        fsm_cs(FSM_DONE),
                    )
                ),
                When(FSM_DONE)(
                    reg_en(Int(1, 1, 2)),
                    fsm_cs(FSM_DONE),
                )
            )
        )
    )
    return m


def make_component_in32(name, num_ports, data_width, data_width_ext):
    m = Module(name)
    ports = {}
    clk = m.Input('clk')
    rst = m.Input('rst')
    start = m.Input('start')
    num_data = m.Input('num_data', 32)
    num_conf = m.Input('num_conf', 32)
    rdy = m.Input('rdy')
    available_read = m.Input('available_read')
    rd_data = m.Input('rd_data', data_width_ext)
    req_rd_data = m.OutputReg('req_rd_data')
    dconf = m.OutputReg('dconf', 32)
    en = m.Output('en')

    for i in range(num_ports):
        ports['rout%d' % (i + 1)] = m.Output('rout%d' % (i + 1))
    for i in range(num_ports):
        ports['dout%d' % (i + 1)] = m.Output('dout%d' % (i + 1), data_width)

    bits = int(math.ceil(math.log((data_width_ext // (data_width * num_ports)), 2)))
    if bits == 0:
        bits = 2
    else:
        bits = bits + 1

    index_conf = m.Reg('index_conf', 4)
    reg_en = m.Reg('reg_en')
    data = m.Reg('data', data_width_ext)
    data_out = m.Reg('data_out', num_ports * data_width)
    r_out = m.Reg('r_out')
    cont_data = m.Reg('cont_data', 32)
    cont_conf = m.Reg('cont_conf', 32)
    fsm_cs = m.Reg('fms_cs', 3)
    flag_cpy_data = m.Reg('flag_cpy_data')
    wconf = m.Wire('wconf', 32, data_width_ext // (32))

    i = m.Genvar('j')
    m.GenerateFor(i(0), i < 16, i.inc(), 'gen_2').Assign(
        wconf[i](data[i * (32):((i * (32)) + (32))])
    )

    for i in range(num_ports):
        ports['rout%d' % (i + 1)].assign(r_out)
        ports['dout%d' % (i + 1)].assign(data_out[i * data_width:i * data_width + data_width])

    en.assign(rdy & reg_en)

    FSM_WAIT = m.Localparam('FSM_WAIT', 0, 3)
    FSM_RD_DATA = m.Localparam('FSM_RD_DATA', 1, 3)
    FSM_DONE = m.Localparam('FSM_DONE', 2, 3)

    m.Always(Posedge(clk))(
        If(rst)(
            req_rd_data(Int(0, 1, 2)),
            dconf(Int(0, dconf.width, 10)),
            index_conf(Int(0, 4, 10)),  
            data(Int(0, data.width, 10)),
            data_out(Int(0, data_out.width, 10)),
            r_out(Int(0, 1, 2)),
            cont_data(Int(0, cont_data.width, 10)),
            cont_conf(Int(0, cont_conf.width, 10)),
            reg_en(Int(0, 1, 2)),
            flag_cpy_data(Int(0, 1, 2)),
            fsm_cs(FSM_WAIT),
        ).Elif(start)(
            req_rd_data(Int(0, 1, 2)),
            reg_en(Int(0, 1, 2)),
            r_out(Int(0, 1, 2)),
            Case(fsm_cs)(
                When(FSM_WAIT)(
                    If(available_read)(
                        req_rd_data(Int(1, 1, 2)),
                        flag_cpy_data(Int(0, 1, 2)),
                        fsm_cs(FSM_RD_DATA)
                    ).Elif(cont_data >= (num_data / num_ports))(
                        fsm_cs(FSM_DONE),
                        reg_en(Int(1, 1, 2))
                    ).Else(
                        fsm_cs(FSM_WAIT)
                    )
                ),
                When(FSM_RD_DATA)(
                    If(cont_conf < num_conf)(  
                        If(index_conf < (data_width_ext // (32)) - 1)(
                            If(flag_cpy_data == Int(0, 1, 2))(
                                data(rd_data),
                                flag_cpy_data(Int(1, 1, 2)),
                                dconf(rd_data[0:32]),
                                cont_conf.inc(),
                                index_conf.inc(),
                            ).Else(
                                dconf(wconf[index_conf]),
                                cont_conf.inc(),
                                index_conf.inc(),
                            ),
                            fsm_cs(FSM_RD_DATA)
                        ).Else(
                            dconf(wconf[index_conf]),
                            cont_conf.inc(),
                            index_conf(Int(0, 4, 10)),
                            If(available_read)(
                                req_rd_data(Int(1, 1, 2)),
                                flag_cpy_data(Int(0, 1, 2)),
                                fsm_cs(FSM_RD_DATA)
                            ).Else(
                                fsm_cs(FSM_WAIT)
                            )
                        )
                    ).Elif(cont_data < (num_data / num_ports))(
                        If(flag_cpy_data == Int(0, 1, 2))(
                            data_out(rd_data[0:(data_width * num_ports)]), 
                            r_out(Int(1, 1, 2)),
                            If(rdy)(
                                reg_en(Int(1, 1, 2)),
                                cont_data.inc(),
                                flag_cpy_data(Int(1, 1, 2))
                            ),
                        ).Else(
                            reg_en(Int(1, 1, 2)),
                            fsm_cs(FSM_WAIT)
                        ),
                    ).Else(
                        reg_en(Int(1, 1, 2)),
                        fsm_cs(FSM_DONE),
                    )
                ),
                When(FSM_DONE)(
                    reg_en(Int(1, 1, 2)),
                    fsm_cs(FSM_DONE),
                )
            )
        )
    )
    return m


def make_component_out(name, num_ports, data_width, data_width_ext):
    m = Module(name)
    ports = {}
    clk = m.Input('clk')
    rst = m.Input('rst')
    start = m.Input('start')
    num_data = m.Input('num_data', 32)
    en = m.Input('en')
    for i in range(num_ports):
        ports['rin%d' % (i + 1)] = m.Input('rin%d' % (i + 1))
    for i in range(num_ports):
        ports['din%d' % (i + 1)] = m.Input('din%d' % (i + 1), data_width)
    available_write = m.Input('available_write')
    req_wr_data = m.OutputReg('req_wr_data')
    wr_data = m.Output('wr_data', data_width_ext)
    rdy = m.OutputReg('rdy', 1)
    done = m.OutputReg('done')

    bits = int(math.ceil(math.log((data_width_ext // (data_width * num_ports)), 2)))
    if bits == 0:
        bits = 2
    else:
        bits = bits + 1
    index_data = m.Reg('index_data', bits)
    data = m.Reg('data', (data_width * num_ports), (data_width_ext // (data_width * num_ports)))
    cont_data = m.Reg('cont_data', 32)

    fsm_cs = m.Reg('fms_cs', 3)

    wr_en = m.Wire('wr_en')
    wr_data_in = m.Wire('wr_data_in', (num_ports * data_width))
    cond = 'en & '
    for i in range(num_ports):
        if i == num_ports - 1:
            cond = cond + 'rin%d' % (i + 1)
        else:
            cond = cond + 'rin%d' % (i + 1) + ' & '

    wr_en.assign(EmbeddedCode(cond))
    cond = '{ '
    for i in range(num_ports):
        if i == num_ports - 1:
            cond = cond + 'din%d' % (num_ports - i) + ' }'
        else:
            cond = cond + 'din%d' % (num_ports - i) + ', '

    wr_data_in.assign(EmbeddedCode(cond))

    i = m.Genvar('i')
    m.GenerateFor(i(0), i < (data_width_ext // (data_width * num_ports)), i.inc(), 'gen_1').Assign(
        wr_data[i * (data_width * num_ports):((i * (data_width * num_ports)) + (data_width * num_ports))](data[i]))

    FSM_WAIT = m.Localparam('FSM_WAIT', 0, 3)
    FSM_WR_DATA = m.Localparam('FSM_WR_DATA', 1, 3)
    FSM_DONE = m.Localparam('FSM_DONE', 2, 3)

    m.Always(Posedge(clk))(
        If(rst)(
            fsm_cs(FSM_WR_DATA),
            index_data(Int(0, bits, 10)),
            req_wr_data(Int(0, 1, 2)),
            done(Int(0, 1, 2)),
            cont_data(Int(0, cont_data.width, 10)),
            rdy(Int(1, rdy.width, 10))
        ).Elif(start)(
            req_wr_data(Int(0, 1, 2)),
            Case(fsm_cs)(
                When(FSM_WR_DATA)(
                    If(cont_data >= (num_data / num_ports))(
                        If(index_data > 0)(
                            If(available_write)(
                                req_wr_data(Int(1, 1, 2)),
                                index_data(Int(0, bits, 10)),
                                fsm_cs(FSM_DONE)
                            )
                        ).Else(
                            fsm_cs(FSM_DONE)
                        )
                    ).Elif(wr_en)(
                        If(index_data < (data_width_ext // (data_width * num_ports)) - 1)(
                            data[index_data](wr_data_in),
                            index_data.inc()
                        ).Elif(available_write)(
                            data[index_data](wr_data_in),
                            req_wr_data(Int(1, 1, 2)),
                            index_data(Int(0, bits, 10))
                        ).Else(
                            data[index_data](wr_data_in),
                            fsm_cs(FSM_WAIT),
                            rdy(Int(0, rdy.width, 10))

                        ),
                        cont_data.inc()
                    )
                ),
                When(FSM_WAIT)(
                    If(available_write)(
                        rdy(Int(1, rdy.width, 10)),
                        req_wr_data(Int(1, 1, 2)),
                        index_data(Int(0, bits, 10)),
                        fsm_cs(FSM_WR_DATA)
                    ).Else(
                        rdy(Int(0, rdy.width, 10))
                    )
                ),
                When(FSM_DONE)(
                    fsm_cs(FSM_DONE),
                    done(Int(1, 1, 2))
                ),
            )
        )
    )
    return m


def make_component_out32(name, num_ports, data_width, data_width_ext):
    m = Module(name)
    ports = {}
    clk = m.Input('clk')
    rst = m.Input('rst')
    start = m.Input('start')
    num_data = m.Input('num_data', 32)
    en = m.Input('en')
    for i in range(num_ports):
        ports['rin%d' % (i + 1)] = m.Input('rin%d' % (i + 1))
    for i in range(num_ports):
        ports['din%d' % (i + 1)] = m.Input('din%d' % (i + 1), data_width)
    available_write = m.Input('available_write')
    req_wr_data = m.OutputReg('req_wr_data')
    wr_data = m.Output('wr_data', data_width_ext)
    rdy = m.OutputReg('rdy', 1)
    done = m.OutputReg('done')

    bits = int(math.ceil(math.log((data_width_ext // (data_width * num_ports)), 2)))
    if bits == 0:
        bits = 2
    else:
        bits = bits + 1
    index_data = m.Reg('index_data', bits)
    data = m.Reg('data', (data_width * num_ports), (data_width_ext // (data_width * num_ports)))
    cont_data = m.Reg('cont_data', 32)

    fsm_cs = m.Reg('fms_cs', 3)

    wr_en = m.Wire('wr_en')
    wr_data_in = m.Wire('wr_data_in', (num_ports * data_width))
    cond = 'en & '
    for i in range(num_ports):
        if i == num_ports - 1:
            cond = cond + 'rin%d' % (i + 1)
        else:
            cond = cond + 'rin%d' % (i + 1) + ' & '

    wr_en.assign(EmbeddedCode(cond))
    cond = '{ '
    for i in range(num_ports):
        if i == num_ports - 1:
            cond = cond + 'din%d' % (num_ports - i) + ' }'
        else:
            cond = cond + 'din%d' % (num_ports - i) + ', '

    wr_data_in.assign(EmbeddedCode(cond))

    i = m.Genvar('i')
    m.GenerateFor(i(0), i < (data_width_ext // (data_width * num_ports)), i.inc(), 'gen_1').Assign(
        wr_data[i * (data_width * num_ports):((i * (data_width * num_ports)) + (data_width * num_ports))](data[i]))

    FSM_WAIT = m.Localparam('FSM_WAIT', 0, 3)
    FSM_WR_DATA = m.Localparam('FSM_WR_DATA', 1, 3)
    FSM_DONE = m.Localparam('FSM_DONE', 2, 3)

    m.Always(Posedge(clk))(
        If(rst)(
            fsm_cs(FSM_WR_DATA),
            index_data(Int(0, bits, 10)),
            req_wr_data(Int(0, 1, 2)),
            done(Int(0, 1, 2)),
            cont_data(Int(0, cont_data.width, 10)),
            rdy(Int(1, rdy.width, 10))
        ).Elif(start)(
            req_wr_data(Int(0, 1, 2)),
            Case(fsm_cs)(
                When(FSM_WR_DATA)(
                    If(cont_data >= (num_data / num_ports))(
                        fsm_cs(FSM_DONE)
                    ).Elif(wr_en)(
                        If(available_write)(
                            data[index_data](wr_data_in),
                            req_wr_data(Int(1, 1, 2)),
                            index_data(Int(0, bits, 10))
                        ).Else(
                            data[index_data](wr_data_in),
                            fsm_cs(FSM_WAIT),
                            rdy(Int(0, rdy.width, 10))
                        ),
                        cont_data.inc()
                    )
                ),
                When(FSM_WAIT)(
                    If(available_write)(
                        rdy(Int(1, rdy.width, 10)),
                        req_wr_data(Int(1, 1, 2)),
                        index_data(Int(0, bits, 10)),
                        fsm_cs(FSM_WR_DATA)
                    ).Else(
                        rdy(Int(0, rdy.width, 10))
                    )
                ),
                When(FSM_DONE)(
                    fsm_cs(FSM_DONE),
                    done(Int(1, 1, 2))
                ),
            )
        )
    )
    return m


def make_component_unary(name, operation):
    m = Module(name)
    N = m.Parameter('N', 16)
    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    rin = m.Input('rin')
    din = m.Input('din', N)
    rout = m.OutputReg('rout', 1)
    dout = m.OutputReg('dout', N)

    m.Always(Posedge(clk), Posedge(rst))(
        If(rst)(
            rout(Int(0, rout.width, 10)),
            dout(0),
        ).Elif(clk & en)(
            If(rin == Int(1, 1, 10))(
                dout(operation(din)),
                rout(Int(1, rout.width, 10))
            ).Else(
                rout(Int(0, rout.width, 10))
            )
        )
    )
    return m


def make_component_binary(name, operation):
    m = Module(name)
    N = m.Parameter('N', 16)
    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din1 = m.Input('din1', N)
    din2 = m.Input('din2', N)
    rin1 = m.Input('rin1')
    rin2 = m.Input('rin2')
    rout = m.OutputReg('rout', 1)
    dout = m.OutputReg('dout', N)
    m.Always(Posedge(clk), Posedge(rst))(
        If(rst)(
            rout(Int(0, rout.width, 10)),
            dout(0),
        ).Elif(clk & en)(
            If((rin1 == Int(1, 1, 10)) & (rin2 == Int(1, 1, 10)))(
                dout(operation(din1, din2)),
                rout(Int(1, rout.width, 10))
            ).Else(
                rout(Int(0, rout.width, 10))
            )
        )
    )
    return m


def make_component_immediate(name, operation):
    m = Module(name)
    N = m.Parameter('N', 16)
    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    ID = m.Parameter('ID', 0)
    dconf = m.Input('dconf', 32)
    rin = m.Input('rin')
    din = m.Input('din', N)
    rout = m.OutputReg('rout', 1)
    dout = m.OutputReg('dout', N)
    immediate = m.Reg('immediate', 24)
    m.Always(Posedge(clk), Posedge(rst))(
        If(rst)(
            rout(Int(0, rout.width, 10)),
            dout(0),
            immediate(ID)
        ).Elif(clk)(
            If(dconf[:8] == ID)(
                immediate(Cat(Int(0, 8, 10), dconf[8:N + 8]))
            ),
            If(en)(
                If(rin == Int(1, 1, 10))(
                    dout(operation(din, immediate)),
                    rout(Int(1, rout.width, 10))
                ).Else(
                    rout(Int(0, rout.width, 10))
                )
            )
        )
    )
    return m


def make_component_accumulator(name, operation, initvalue):
    m = Module(name)
    N = m.Parameter('N', 16)
    ID = m.Parameter('ID', 0)
    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    dconf = m.Input('dconf', 32)
    rin = m.Input('rin')
    din = m.Input('din', N)
    rout = m.OutputReg('rout', 1)
    dout = m.OutputReg('dout', N)
    acc = m.Reg('acc', N)
    immediate = m.Reg('immediate', 24)
    count = m.Reg('count', 25)
    m.Always(Posedge(clk), Posedge(rst))(
        If(rst)(
            rout(Int(0, rout.width, 10)),
            dout(0),
            acc(initvalue),
            immediate(ID),
            count(Int(0, count.width, 10))
        ).Elif(clk)(
            If(dconf[:8] == ID)(
                immediate(dconf[8:32])
            ),
            If(en)(
                If(rin == Int(1, 1, 10))(
                    If(count < immediate - 1)(
                        acc(operation(acc, din)),
                        count.inc(),
                        rout(Int(0, rout.width, 10))
                    ).Else(
                        count(Int(0, count.width, 10)),
                        acc(initvalue),
                        rout(Int(1, rout.width, 10)),
                        dout(operation(acc, din))
                    )

                )
            )
        )
    )
    return m

def make_component_histogram(name):
    m = Module(name)
    N = m.Parameter('N', 16)
    ID = m.Parameter('ID', 0)
    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    dconf = m.Input('dconf', 32)
    rin = m.Input('rin')
    din = m.Input('din', N)
    rout = m.OutputReg('rout', 1)
    dout = m.OutputReg('dout', N)

    counter_out = m.Reg('counter_out', 9)
    immediate = m.Reg('immediate', 24)
    counter_in = m.Reg('counter_in', 24)
    reseted = m.Reg('reseted', 256)
    histogram = m.Reg('histogram',16,256)

    m.Always(Posedge(clk), Posedge(rst))(
        If(rst)(
            rout(Int(0, rout.width, 10)),
            dout(0),
            immediate(ID),
            counter_in(Int(0,counter_in.width,10)),
            counter_out(Int(0,counter_out.width,10)),
            reseted(Int(0,reseted.width,10)),
        ).Elif(clk)(
            If(dconf[:8] == ID)(
                immediate(dconf[8:32])
            ),
            If(en)(
                If(AndList(rin == Int(1, 1, 10),counter_in < immediate))(
                    If(reseted[din] == 0)(
                        histogram[din](Int(1, 1, 10)),
                        reseted[din](Int(1, 1, 10)),
                    ).Else(
                        histogram[din](histogram[din] + 1),
                    ),
                    rout(Int(0,rout.width,10)),
                    counter_in.inc(),
                ).Elif(counter_out < Int(histogram.length,9,10))(
                    dout(Mux(reseted[counter_out]==0,0,histogram[counter_out])),
                    rout(Int(1, rout.width, 10)),
                    counter_out.inc(),
                ).Else(
                    rout(Int(0, rout.width, 10)),
                )
            )
        )
    )
    return m

def add(a, b):
    return a + b


def sub(a, b):
    return a - b


def mul(a, b):
    return a * b


def reg(a):
    return a


def min(a, b):
    return Mux(a < b, a, b)


def max(a, b):
    return Mux(a > b, a, b)

def slt(a, b):
    return a << b


def div(a, b):
    return Mux(b == 0, 0, a / b)  


def functions(functionname):
    funcs = {'add_add': add, 'add_addi': add, 'add_sub': sub, 'add_subi': sub, 'add_mul': mul, 'add_muli': mul,
             'add_div': div, 'add_divi': div,
             'add_register': reg, 'add_accmin': min, 'add_accmax': max, 'add_accadd': add,"add_slt":slt}
    return funcs[functionname]
