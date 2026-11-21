package ar.edu.uns.cs.ed;

import ar.edu.uns.cs.ed.tdas.excepciones.*;
import ar.edu.uns.cs.ed.tdas.tdalista.*;
import ar.edu.uns.cs.ed.tdas.tdamapeo.*;
import ar.edu.uns.cs.ed.tdas.Position;
import ar.edu.uns.cs.ed.tdas.tdaarbol.*;
import ar.edu.uns.cs.ed.tdas.tdadiccionario.diccionarioConHashAbierto;
import ar.edu.uns.cs.ed.tdas.tdapila.*;

/**
 * Incluir los import necesarios para las clases utilizadas en el código.
 */

/**
 * Clase que representa el motor de deliveración.
 * 
 */
public class MotorDeliberacion {
    
    /**
     * Incluir los atributos necesarios para el desarrollo.
     */
    private Arbol<Opinion> arbol;
    private MapeoConHashAbierto<String,PositionList<Opinion>> mapeoUsuario;
    private MapeoConHashAbierto<String,Position<Opinion>> mapeoPosteos;
    private Stack<Operaciones> operaciones;

    /**
     * Constructor de la clase MotorDeliveracion.
     * Inicializa el árbol de debate, y las estructuras correspondientes.  
     * 
     */
    public MotorDeliberacion() {
        arbol = new Arbol<>();
        mapeoUsuario = new MapeoConHashAbierto<>();
        mapeoPosteos = new MapeoConHashAbierto<>();
        operaciones = new pilaConArreglo<>();
    //TODO: Inicializar el árbol de debate y las estructuras correspondientes. 
    }

    /**
     * Registra una operación en la estructura que almacena las operaciones.
     * Las siguientes operaciones se pueden deshacer: "CREAR_DEBATE", "POSTEAR". El resto de las operaciones no se pueden deshacer.
     * 
     * La operación eliminar posteo no se puede deshacer, y si se intenta deshacer una operación cuando no hay operaciones registradas, se lanza una excepción.
     * 
     * 
     * @param operacion : La operación a registrar. 1: CREAR_DEBATE, 2: POSTEAR
     * @param detalle : Detalle de la operación, como el id del argumento o el id del usuario. El texto de la operación se almacena en mayúsculas para mantener consistencia. Por ejemplo: "CREAR_DEBATE:" + idRaiz.trim()
     * @param idArgumento : Identificador del argumento o de root en caso de ser el tema raíz.
     * @throws IllegalArgumentException si la operación es nula o vacía.
     */
    public void registrarOperacion(int operacion, String detalle, String idArgumento){
        //TODO: Implementar el registro de la operación en la estructura correspondiente.
        if (operacion != 1 && operacion != 2) {
            throw new IllegalArgumentException("Operación no válida");
        }

        String detalleNormalizado = detalle.trim().toUpperCase();
        operaciones.push(new Operaciones(operacion, detalleNormalizado, idArgumento.trim()));
    }

/**
 * Deshace la última operación registrada en el motor de deliveración.
 * La operación eliminar posteo no se puede deshacer, y si se intenta deshacer una operación cuando no hay operaciones registradas, se lanza una excepción.
 * @return
 * @throws EmptyStackException
 */
    public String deshacerUltimaOperacion() throws EmptyStackException {
        //TODO: Implementar la funcionalidad de deshacer la última operación registrada.
        if(operaciones.isEmpty()){
            throw new EmptyStackException("No hay operaciones hechas");
        }
        Operaciones ultimaOp = operaciones.pop();
        int tipo = ultimaOp.getOperacion();
        String idPosteo = ultimaOp.getId();
        String toRet = "";

        Position<Opinion> posicion = mapeoPosteos.remove(idPosteo);
        if(posicion == null){
            toRet = "No se puedo deshacer: el posteo " + idPosteo + " ya no existe.";
        }else{
            arbol.removeNode(posicion);
            if(tipo == 1){
                toRet = "Se deshizo la creacion del debate: " + idPosteo;
            }else if (tipo == 2){
                toRet = "Se deshizo el posteo: " + idPosteo;
            }
        }
        return toRet;
    }
        
        
/**
 * Crea un nuevo debate con un argumento raíz.
 * @param idRaiz   : Identificador único del tema raíz.
 * @param texto:    Texto del tema raíz.
 * @param idUsuario: Identificador del usuario.
 * @param nombreUsuario: Nombre del usuario.
 * 
 * El método valida que los parámetros no sean nulos o vacíos, y que el identificador de la raíz sea numérico.
 * Si el árbol ya tiene un debate, lanza una excepción.
 * El método registra la operación en la pila de operaciones para permitir deshacerla posteriormente.
 * @throws IllegalArgumentException si algún parámetro es inválido.
 * @throws IllegalStateException si ya existe un debate en curso.
 * Crea un usuario y un tema raíz, y los agrega al árbol y a las estructuras correspondientes.
 */
    public void crearDebate(String idRaiz, String texto, String idUsuario, String nombreUsuario) {
    //TODO: Implementar la creación del debate con el argumento raíz y el usuario correspondiente.
        if(idRaiz == null || texto == null || idUsuario == null || nombreUsuario == null) {
            throw new IllegalArgumentException("Alguno de los parámetros es nulo");
        }

        if(!arbol.isEmpty()){
            throw new IllegalStateException("El árbol ya tiene un debate");
        }

        //creando tema de debate

        Usuario user = new Usuario (idUsuario, nombreUsuario);
        Opinion raizOp = new Opinion (idRaiz,texto,"ROOT",user);
        arbol.createRoot(raizOp);

        //1) agregando opinion inicio de debate del usuario a su lista de posteos
        //2) agreando id con la lista de posteos del usuario al mapeo.
        // mapeo de usarios --> posteos
        
        PositionList<Opinion> posteosUser = new LDE<>();
        posteosUser.addLast(raizOp);
        mapeoUsuario.put(idUsuario,posteosUser);

        // agregando la posicion de la opinion en el arbol al mapeo.
        // mapeo de posteos --> posicion en el árbol

        mapeoPosteos.put(idRaiz,arbol.root());
        
        // registrando la operacion en el "historial"

        registrarOperacion(1, "CREAR DEBATE" + idRaiz.trim(), idRaiz);
    }

    public void Postear(Usuario usr, String idNuevo, String idPadre, String postura, String texto) {
        //TODO: Implementar la funcionalidad de postear un nuevo argumento en el debate.
    }
    

    // Elimina un posteo y todo el subárbol.
    public void eliminarPosteo(String idUsuario, String idPosteo) {
    //TODO: Implementar la funcionalidad de eliminar un posteo y todo su subárbol en el debate.
         if (idUsuario == null || idPosteo == null) {
        throw new IllegalArgumentException("El usuario y el posteo no pueden ser nulos.");
    }
    if (arbol.isEmpty()) {
        throw new IllegalStateException("No hay un debate activo.");
    }
    try{
     Position<Opinion> raizSubarbol = mapeoPosteos.get(idPosteo);
        if (raizSubarbol == null) {
            throw new IllegalArgumentException("El posteo no existe.");}
        if (!raizSubarbol.element().getUsuario().getId().equals(idUsuario)) {
            throw new IllegalArgumentException("El usuario no tiene permiso para eliminar este posteo.");
        }
        PositionList<String> ids = new LDE<>();
        PositionList<Position<Opinion>> nodos = new LDE<>();
        obtenerIdsYPosicionesSubarbol(raizSubarbol, ids, nodos);
        for (Position<Opinion> nodo : nodos) {
            Opinion op = nodo.element();

            mapeoPosteos.remove(op.getIdArgumento());

            PositionList<Opinion> listaUsuario = mapeoUsuario.get(op.getUsuario().getId());
            if (listaUsuario != null) {
                 for (Position<Opinion> p : listaUsuario.positions()) {
                if (p.element() == op) {
                listaUsuario.remove(p);
                return;}
            }

                arbol.removeNode(nodo);
            }
        }
        } catch (InvalidKeyException | InvalidPositionException e) {
        throw new IllegalStateException("Error al eliminar el posteo: " + e.getMessage());
        }
    }|

    //obtiene las posiciones en postorden.
    private void obtenerIdsYPosicionesSubarbol(Position<Opinion> nodo, PositionList<String> ids, PositionList<Position<Opinion>> posiciones) {
    //TODO: Implementar la funcionalidad de obtener los ids y posiciones de un subárbol en postorden.
        for (Position<Opinion> hijo : arbol.children(nodo)) {
            obtenerIdsYPosicionesSubarbol(hijo, ids, posiciones);
        }
        ids.addLast(nodo.element().getIdArgumento());
        posiciones.addLast(nodo);
    }

    //limpia todo el debate, dejando la estructura vacía.   
    public void clearDebate() {
    //TODO: Implementar la funcionalidad de limpiar todo el debate, dejando la estructura vacía.
    }

    //consultar por usuario, devuelve una lista de argumentos que pertenecen a ese usuario.
    public PositionList<Opinion> consultarArgumentosPorUsuario(String idUsuario) {
    //TODO: Implementar la funcionalidad de consultar los argumentos de un usuario específico en el debate.
    }

    /**
     * Calcula la polarización de todos los nodos del debate.
     * La polarización se define como la cantidad de argumentos a favor y en contra que existen en el subárbol que tiene como raíz cada nodo.
     * @throws IllegalStateException si no hay un debate activo.
     * @return Un diccionario cuya clave es el ID del nodo y el valor es un diccionario con la polarización: 
     * "AFAVOR" y "ENCONTRA" y sus respectivos conteos.
     */
    public diccionarioConHashAbierto<String,diccionarioConHashAbierto<String,Integer>> calcularPolarizacionDebate(){
        //TODO
    }

     /**
    * Calcula la polarización de un nodo específico en el árbol de debate.
    * La polarización se define como la cantidad de argumentos a favor y en contra que existen en el subárbol que tiene como raíz el nodo especificado por idNodo.
    * @throws IllegalStateException si no hay un debate activo. 
    * @param idNodo : Identificador del nodo para el cual se desea calcular la polarización.
    * @return Un diccionarioConHashAbierto<String,Integer> que contiene la polarización del nodo, con las claves "AFAVOR" y "ENCONTRA" y sus respectivos conteos.
    */
    public diccionarioConHashAbierto<String,Integer> calcularPolarizacionNodo(String idNodo){
        //TODO

    }
 /*** Mostrar árbol por consola ***/
 
    /**
     * Imprime por consola, en modo texto, el contenido del árbol de debate,
     * con indentación jerárquica que muestra la estructura padre-hijo.
     * Cada nodo se muestra con su id, postura, usuario y texto del argumento.
     */
    public void imprimirDebate() {
        if (arbol == null || arbol.isEmpty()) {
            System.out.println("(El árbol de debate está vacío.)");
            return;
        }

        try {
            Position<Opinion> raiz = arbol.root();
            System.out.println(formatearNodo(raiz.element()));
            imprimirHijos(raiz, "");
        } catch (Exception e) {
            System.out.println("No se pudo imprimir el árbol: " + e.getMessage());
        }
    }

    /**
     * Imprime recursivamente los hijos de 'nodo', usando 'prefijo' para
     * mantener la indentación acumulada de los niveles superiores y
     * conectores tipo "├──" / "└──" para el nivel actual.
     */
    private void imprimirHijos(Position<Opinion> nodo, String prefijo) throws InvalidPositionException {
        PositionList<Position<Opinion>> hijos = new LDE<Position<Opinion>>();
        for (Position<Opinion> hijo : arbol.children(nodo)) {
            hijos.addLast(hijo);
        }


        for (Position<Opinion> hijo : arbol.children(nodo)) {
            boolean esUltimo = (hijo == hijos.last().element());
            
            String conector = esUltimo ? "└── " : "├── ";
            System.out.println(prefijo + conector + formatearNodo(hijo.element()));

            String prefijoHijo = prefijo + (esUltimo ? "    " : "│   ");
            imprimirHijos(hijo, prefijoHijo);
            
        }
    }

/** Arma la línea de texto que representa a un Argumento en la impresión. */
    private String formatearNodo(Opinion a) {
        if (a == null) {
            return "(argumento nulo)";
        }
        return "[" + a.getIdArgumento() + "] (" + a.getPostura() + ") "
                + "usuario=" + a.getUsuario().getId() + " -> " + a.getTexto();
    }
}


