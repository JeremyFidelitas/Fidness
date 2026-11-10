package fidness.vista;

import fidness.excepcion.FidnessException;
import fidness.modelo.Ejercicio;
import fidness.modelo.GrupoMuscular;
import fidness.modelo.Usuario;
import fidness.servicio.SistemaFidness;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class VentanaFidness extends JFrame {
    private final SistemaFidness sistema;
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);
    private final Font fuente = new Font("Serif", Font.PLAIN, 17);
    private final Font titulo = new Font("Serif", Font.BOLD, 27);
    private final JTextField usuario = new JTextField(22);
    private final JPasswordField clave = new JPasswordField(22);
    private final JLabel mensajeIngreso = new JLabel(" ", SwingConstants.CENTER);
    private final JTextField nombre = new JTextField();
    private final JComboBox<GrupoMuscular> grupo = new JComboBox<>(GrupoMuscular.values());
    private final JTextArea instrucciones = new JTextArea(7, 25);
    private final JLabel mensajeAdmin = new JLabel(" ");
    private final JLabel estadoEdicion = new JLabel("Nuevo ejercicio");
    private final DefaultTableModel modeloAdmin = crearModelo("Ejercicio", "Grupo", "Estado");
    private final JTable tablaAdmin = new JTable(modeloAdmin);
    private final JButton desactivar = boton("Desactivar", "desactivar");
    private final JTextField filtroNombre = new JTextField();
    private final JComboBox<Object> filtroGrupo = new JComboBox<>();
    private final DefaultTableModel modeloCatalogo = crearModelo("Ejercicio", "Grupo muscular");
    private final JTable tablaCatalogo = new JTable(modeloCatalogo);
    private final JLabel mensajeCatalogo = new JLabel(" ");
    private List<Ejercicio> listaAdmin = new ArrayList<>();
    private List<Ejercicio> listaCatalogo = new ArrayList<>();
    private Integer idSeleccionado;
    private boolean cargandoTabla;
    private final JButton ingresar = boton("Ingresar", "ingresar");

    public VentanaFidness(SistemaFidness sistema) {
        super("Fidness / Ingreso");
        this.sistema = sistema;
        javax.swing.UIManager.put("OptionPane.yesButtonText", "Sí");
        javax.swing.UIManager.put("OptionPane.noButtonText", "No");
        javax.swing.UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        javax.swing.UIManager.put("OptionPane.okButtonText", "Aceptar");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1080, 650);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        contenido.add(crearIngreso(), "ingreso");
        contenido.add(crearAdministracion(), "admin");
        contenido.add(crearCatalogo(), "catalogo");
        setContentPane(contenido);
        aplicarFuente(contenido);
        getRootPane().setDefaultButton(ingresar);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent evento) {
                sistema.cerrarSesion();
                clave.setText("");
            }
        });
    }

    private JPanel panel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        return panel;
    }

    private JButton boton(String texto, String identificador) {
        JButton boton = new JButton(texto);
        boton.setName(identificador);
        boton.setBackground(Color.WHITE);
        boton.setFocusPainted(true);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)));
        return boton;
    }

    private DefaultTableModel crearModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private void aplicarFuente(Component componente) {
        componente.setFont(fuente);
        if (componente instanceof java.awt.Container) {
            for (Component hijo : ((java.awt.Container) componente).getComponents()) {
                aplicarFuente(hijo);
            }
        }
        if (componente instanceof JLabel && "titulo".equals(componente.getName())) {
            componente.setFont(titulo);
        }
    }

    private JLabel encabezado(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setName("titulo");
        etiqueta.setFont(titulo);
        return etiqueta;
    }

    private void campo(JComponent componente, String identificador) {
        componente.setName(identificador);
        componente.setPreferredSize(new Dimension(280, 36));
    }

    private JPanel crearIngreso() {
        JPanel pagina = panel();
        pagina.setLayout(new GridBagLayout());
        JPanel formulario = panel();
        formulario.setLayout(new GridBagLayout());
        GridBagConstraints posicion = new GridBagConstraints();
        posicion.gridx = 0;
        posicion.fill = GridBagConstraints.HORIZONTAL;
        posicion.weightx = 1;
        posicion.insets = new Insets(7, 0, 7, 0);
        posicion.gridy = 0;
        JLabel tituloIngreso = encabezado("Iniciar sesión");
        tituloIngreso.setHorizontalAlignment(SwingConstants.CENTER);
        formulario.add(tituloIngreso, posicion);
        posicion.gridy++;
        formulario.add(new JLabel("Usuario"), posicion);
        campo(usuario, "usuario");
        posicion.gridy++;
        formulario.add(usuario, posicion);
        posicion.gridy++;
        formulario.add(new JLabel("Contraseña"), posicion);
        campo(clave, "clave");
        posicion.gridy++;
        formulario.add(clave, posicion);
        posicion.gridy++;
        formulario.add(ingresar, posicion);
        mensajeIngreso.setName("mensajeIngreso");
        posicion.gridy++;
        formulario.add(mensajeIngreso, posicion);
        formulario.setPreferredSize(new Dimension(470, 360));
        pagina.add(formulario);
        ingresar.addActionListener(evento -> iniciarSesion());
        return pagina;
    }

    private void iniciarSesion() {
        char[] contraseña = clave.getPassword();
        try {
            Usuario cuenta = sistema.iniciarSesion(usuario.getText(), contraseña);
            mensajeIngreso.setText(" ");
            getRootPane().setDefaultButton(null);
            if (cuenta.esAdministrador()) {
                cargarAdministracion();
                limpiarFormulario();
                tarjetas.show(contenido, "admin");
                setTitle("Fidness / Ejercicios");
            } else {
                filtroNombre.setText("");
                filtroGrupo.setSelectedIndex(0);
                buscar();
                tarjetas.show(contenido, "catalogo");
                setTitle("Fidness / Catálogo");
            }
        } catch (FidnessException ex) {
            sistema.cerrarSesion();
            mensajeIngreso.setText(ex.getMessage());
        } finally {
            Arrays.fill(contraseña, '\0');
            clave.setText("");
        }
    }

    private JPanel barra(String seccion, String perfil, boolean mostrarRutinas) {
        JPanel barra = panel();
        barra.setLayout(new BorderLayout(20, 0));
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        JPanel izquierda = panel();
        izquierda.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));
        izquierda.add(encabezado(seccion));
        if (mostrarRutinas) {
            JButton rutinas = boton("Mis rutinas", "misRutinas");
            rutinas.setEnabled(false);
            rutinas.setToolTipText("Disponible en el próximo avance.");
            izquierda.add(rutinas);
        }
        JPanel derecha = panel();
        derecha.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        derecha.add(new JLabel(perfil));
        JButton salir = boton("Cerrar sesión", "salir" + perfil);
        salir.addActionListener(evento -> cerrarSesion());
        derecha.add(salir);
        barra.add(izquierda, BorderLayout.WEST);
        barra.add(derecha, BorderLayout.EAST);
        return barra;
    }

    private void cerrarSesion() {
        sistema.cerrarSesion();
        usuario.setText("");
        clave.setText("");
        mensajeIngreso.setText(" ");
        modeloAdmin.setRowCount(0);
        modeloCatalogo.setRowCount(0);
        listaAdmin = new ArrayList<>();
        listaCatalogo = new ArrayList<>();
        limpiarFormulario();
        tarjetas.show(contenido, "ingreso");
        setTitle("Fidness / Ingreso");
        getRootPane().setDefaultButton(ingresar);
        usuario.requestFocusInWindow();
    }

    private void prepararTabla(JTable tabla, String identificador) {
        tabla.setName(identificador);
        tabla.setRowHeight(33);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setShowGrid(true);
        tabla.setGridColor(Color.LIGHT_GRAY);
        tabla.setFillsViewportHeight(true);
        tabla.setBackground(Color.WHITE);
        tabla.setSelectionBackground(new Color(226, 226, 226));
        tabla.setSelectionForeground(Color.BLACK);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setBackground(Color.WHITE);
    }

    private JPanel crearAdministracion() {
        JPanel pagina = panel();
        pagina.setLayout(new BorderLayout());
        pagina.add(barra("Ejercicios", "Administrador", false), BorderLayout.NORTH);
        JPanel izquierda = panel();
        izquierda.setLayout(new BorderLayout(10, 15));
        izquierda.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 12));
        izquierda.add(new JLabel("Lista de ejercicios"), BorderLayout.NORTH);
        prepararTabla(tablaAdmin, "tablaAdmin");
        tablaAdmin.getColumnModel().getColumn(0).setPreferredWidth(250);
        tablaAdmin.getColumnModel().getColumn(1).setPreferredWidth(90);
        tablaAdmin.getColumnModel().getColumn(2).setPreferredWidth(75);
        izquierda.add(new JScrollPane(tablaAdmin), BorderLayout.CENTER);
        JPanel acciones = panel();
        acciones.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton nuevo = boton("Nuevo", "nuevo");
        nuevo.addActionListener(evento -> limpiarFormulario());
        desactivar.addActionListener(evento -> desactivarSeleccionado());
        acciones.add(nuevo);
        acciones.add(desactivar);
        izquierda.add(acciones, BorderLayout.SOUTH);

        JPanel formulario = panel();
        formulario.setLayout(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(20, 15, 15, 20));
        GridBagConstraints p = new GridBagConstraints();
        p.gridx = 0;
        p.weightx = 1;
        p.fill = GridBagConstraints.HORIZONTAL;
        p.insets = new Insets(4, 0, 4, 0);
        p.gridy = 0;
        formulario.add(estadoEdicion, p);
        p.gridy++;
        formulario.add(new JLabel("Nombre"), p);
        campo(nombre, "nombreEjercicio");
        p.gridy++;
        formulario.add(nombre, p);
        p.gridy++;
        formulario.add(new JLabel("Grupo muscular"), p);
        campo(grupo, "grupoEjercicio");
        p.gridy++;
        formulario.add(grupo, p);
        p.gridy++;
        formulario.add(new JLabel("Instrucciones"), p);
        instrucciones.setName("instrucciones");
        instrucciones.setLineWrap(true);
        instrucciones.setWrapStyleWord(true);
        instrucciones.setMargin(new Insets(8, 8, 8, 8));
        p.gridy++;
        p.fill = GridBagConstraints.BOTH;
        p.weighty = 1;
        formulario.add(new JScrollPane(instrucciones), p);
        p.weighty = 0;
        p.fill = GridBagConstraints.HORIZONTAL;
        p.gridy++;
        JPanel guardarCancelar = panel();
        guardarCancelar.setLayout(new GridLayout(1, 2, 15, 0));
        JButton guardar = boton("Guardar", "guardar");
        guardar.addActionListener(evento -> guardarEjercicio());
        JButton cancelar = boton("Cancelar", "cancelar");
        cancelar.addActionListener(evento -> cancelarEdicion());
        guardarCancelar.add(guardar);
        guardarCancelar.add(cancelar);
        formulario.add(guardarCancelar, p);
        JSplitPane division = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, izquierda, formulario);
        division.setBorder(null);
        division.setDividerSize(1);
        division.setResizeWeight(0.42);
        division.setDividerLocation(440);
        pagina.add(division, BorderLayout.CENTER);
        mensajeAdmin.setName("mensajeAdmin");
        mensajeAdmin.setBorder(BorderFactory.createEmptyBorder(8, 20, 12, 20));
        pagina.add(mensajeAdmin, BorderLayout.SOUTH);
        tablaAdmin.getSelectionModel().addListSelectionListener(evento -> {
            if (!evento.getValueIsAdjusting() && !cargandoTabla) { seleccionarEjercicio(); }
        });
        return pagina;
    }

    private void cargarAdministracion() throws FidnessException {
        cargandoTabla = true;
        try {
            listaAdmin = sistema.listarEjercicios();
            modeloAdmin.setRowCount(0);
            for (Ejercicio ejercicio : listaAdmin) {
                modeloAdmin.addRow(new Object[]{ejercicio.getNombre(), ejercicio.getGrupoMuscular(),
                    ejercicio.isActivo() ? "Activo" : "Inactivo"});
            }
        } finally {
            cargandoTabla = false;
        }
    }

    private void seleccionarEjercicio() {
        int fila = tablaAdmin.getSelectedRow();
        if (fila < 0 || fila >= listaAdmin.size()) { return; }
        Ejercicio ejercicio = listaAdmin.get(fila);
        idSeleccionado = ejercicio.getIdEjercicio();
        nombre.setText(ejercicio.getNombre());
        grupo.setSelectedItem(ejercicio.getGrupoMuscular());
        instrucciones.setText(ejercicio.getInstrucciones());
        instrucciones.setCaretPosition(0);
        estadoEdicion.setText(ejercicio.isActivo() ? "Editar ejercicio" : "Editar ejercicio inactivo");
        desactivar.setEnabled(ejercicio.isActivo());
        mensajeAdmin.setText(" ");
    }

    private void limpiarFormulario() {
        tablaAdmin.clearSelection();
        idSeleccionado = null;
        nombre.setText("");
        grupo.setSelectedIndex(-1);
        instrucciones.setText("");
        estadoEdicion.setText("Nuevo ejercicio");
        desactivar.setEnabled(false);
        mensajeAdmin.setText(" ");
    }

    private void cancelarEdicion() {
        if (tablaAdmin.getSelectedRow() >= 0) { seleccionarEjercicio(); }
        else { limpiarFormulario(); }
        mensajeAdmin.setText("Cambios descartados.");
    }

    private void guardarEjercicio() {
        try {
            int id;
            if (idSeleccionado == null) {
                id = sistema.registrarEjercicio(nombre.getText(),
                        (GrupoMuscular) grupo.getSelectedItem(), instrucciones.getText()).getIdEjercicio();
            } else {
                id = idSeleccionado;
                sistema.actualizarEjercicio(id, nombre.getText(),
                        (GrupoMuscular) grupo.getSelectedItem(), instrucciones.getText());
            }
            cargarAdministracion();
            seleccionarPorId(id);
            mensajeAdmin.setText("Ejercicio guardado.");
        } catch (FidnessException ex) {
            mensajeAdmin.setText(ex.getMessage());
        }
    }

    private void seleccionarPorId(int id) {
        for (int fila = 0; fila < listaAdmin.size(); fila++) {
            if (listaAdmin.get(fila).getIdEjercicio() == id) {
                tablaAdmin.setRowSelectionInterval(fila, fila);
                tablaAdmin.scrollRectToVisible(tablaAdmin.getCellRect(fila, 0, true));
                return;
            }
        }
    }

    private void desactivarSeleccionado() {
        if (idSeleccionado == null) {
            mensajeAdmin.setText("Seleccione un ejercicio de la lista.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Desactivar este ejercicio? Dejará de aparecer en el catálogo.",
                "Desactivar ejercicio", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) { return; }
        try {
            int id = idSeleccionado;
            sistema.desactivarEjercicio(id);
            cargarAdministracion();
            seleccionarPorId(id);
            mensajeAdmin.setText("Ejercicio desactivado.");
        } catch (FidnessException ex) {
            mensajeAdmin.setText(ex.getMessage());
        }
    }

    private JPanel crearCatalogo() {
        JPanel pagina = panel();
        pagina.setLayout(new BorderLayout());
        pagina.add(barra("Catálogo", "Cliente", true), BorderLayout.NORTH);
        JPanel cuerpo = panel();
        cuerpo.setLayout(new BorderLayout(12, 20));
        cuerpo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel filtros = panel();
        filtros.setLayout(new GridBagLayout());
        GridBagConstraints p = new GridBagConstraints();
        p.fill = GridBagConstraints.HORIZONTAL;
        p.insets = new Insets(4, 0, 4, 18);
        p.weightx = 0.55;
        p.gridx = 0;
        p.gridy = 0;
        filtros.add(new JLabel("Nombre del ejercicio"), p);
        p.gridx = 1;
        p.weightx = 0.3;
        filtros.add(new JLabel("Grupo muscular"), p);
        campo(filtroNombre, "filtroNombre");
        p.gridx = 0;
        p.gridy = 1;
        p.weightx = 0.55;
        filtros.add(filtroNombre, p);
        filtroGrupo.addItem("Todos");
        for (GrupoMuscular valor : GrupoMuscular.values()) { filtroGrupo.addItem(valor); }
        campo(filtroGrupo, "filtroGrupo");
        p.gridx = 1;
        p.weightx = 0.3;
        filtros.add(filtroGrupo, p);
        JButton buscar = boton("Buscar", "buscar");
        buscar.addActionListener(evento -> buscar());
        filtroNombre.addActionListener(evento -> buscar());
        p.gridx = 2;
        p.weightx = 0.15;
        p.insets = new Insets(4, 0, 4, 0);
        filtros.add(buscar, p);
        cuerpo.add(filtros, BorderLayout.NORTH);
        prepararTabla(tablaCatalogo, "tablaCatalogo");
        tablaCatalogo.getColumnModel().getColumn(0).setPreferredWidth(720);
        tablaCatalogo.getColumnModel().getColumn(1).setPreferredWidth(320);
        cuerpo.add(new JScrollPane(tablaCatalogo), BorderLayout.CENTER);
        JPanel pie = panel();
        pie.setLayout(new BorderLayout(10, 0));
        mensajeCatalogo.setName("mensajeCatalogo");
        pie.add(mensajeCatalogo, BorderLayout.CENTER);
        JPanel botones = panel();
        botones.setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        JButton ver = boton("Ver instrucciones", "verInstrucciones");
        ver.addActionListener(evento -> verInstrucciones());
        JButton rutina = boton("Crear rutina", "crearRutina");
        rutina.setEnabled(false);
        rutina.setToolTipText("Disponible en el próximo avance.");
        botones.add(ver);
        botones.add(rutina);
        pie.add(botones, BorderLayout.EAST);
        cuerpo.add(pie, BorderLayout.SOUTH);
        pagina.add(cuerpo, BorderLayout.CENTER);
        return pagina;
    }

    private void buscar() {
        try {
            Object seleccion = filtroGrupo.getSelectedItem();
            GrupoMuscular grupoBuscado = seleccion instanceof GrupoMuscular
                    ? (GrupoMuscular) seleccion : null;
            listaCatalogo = sistema.buscarEjercicios(filtroNombre.getText(), grupoBuscado);
            modeloCatalogo.setRowCount(0);
            for (Ejercicio ejercicio : listaCatalogo) {
                modeloCatalogo.addRow(new Object[]{ejercicio.getNombre(), ejercicio.getGrupoMuscular()});
            }
            mensajeCatalogo.setText(listaCatalogo.isEmpty()
                    ? "No hay ejercicios que coincidan con la búsqueda."
                    : listaCatalogo.size() + " ejercicios encontrados.");
        } catch (FidnessException ex) {
            mensajeCatalogo.setText(ex.getMessage());
        }
    }

    private void verInstrucciones() {
        int fila = tablaCatalogo.getSelectedRow();
        if (fila < 0) {
            mensajeCatalogo.setText("Seleccione un ejercicio de la lista.");
            return;
        }
        Ejercicio ejercicio = listaCatalogo.get(fila);
        JTextArea detalle = new JTextArea(ejercicio.getInstrucciones(), 8, 38);
        detalle.setLineWrap(true);
        detalle.setWrapStyleWord(true);
        detalle.setEditable(false);
        detalle.setFont(fuente);
        detalle.setMargin(new Insets(10, 10, 10, 10));
        detalle.setCaretPosition(0);
        JPanel panel = panel();
        panel.setLayout(new BorderLayout(0, 12));
        JLabel etiqueta = new JLabel(ejercicio.getNombre() + " / " + ejercicio.getGrupoMuscular());
        etiqueta.setFont(titulo);
        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(new JScrollPane(detalle), BorderLayout.CENTER);
        JOptionPane.showMessageDialog(this, panel, "Instrucciones", JOptionPane.PLAIN_MESSAGE);
    }
}
