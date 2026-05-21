# Analisis de tablas y clases del proyecto

Este resumen cruza las tablas SQL compartidas con las clases reales encontradas en `src/main/java`.

## Mapeo por tabla

### `Users`
- Modelo: `Models.User`
- Controlador: `Controllers.UserController`
- Persistencia: `Repositories.IUserRepository`, `Repositories.UserRepository`, `DAO.UserDAO`
- Operaciones principales: `registerUser`, `createUser`, `loginUser`, `searchUsers`, `findUserById`, `updateUser`, `deleteUser`

### `DocumentTypes`
- No existe una clase `Models.DocumentType`.
- Se usa de forma indirecta desde `Models.Client` con `idDocumentType` y `documentTypeName`.
- La carga de opciones se hace con `listDocumentTypes()` en `IClientRepository`, `ClientRepository`, `ClientDAO` y `ClientController`.

### `Categories`
- Modelo: `Models.Category`
- Controlador: `Controllers.CategoryController`
- Persistencia: `DAO.CategoryDAO`
- Operaciones principales: `listCategories`, `searchCategories`, `createCategory`, `updateCategory`, `deleteCategory`

### `Products`
- Modelo: `Models.Product`
- Controlador: `Controllers.ProductController`
- Persistencia: `DAO.ProductDAO`
- Operaciones principales: `listProducts`, `searchProducts`, `listCategories`, `createProduct`, `updateProduct`, `deleteProduct`

### `Warehouses`
- Modelo: `Models.Warehouses`
- Controlador: `Controllers.AlmacenesController`
- Persistencia: `Repositories.IWarehousesRepository`, `Repositories.WarehousesRepository`, `DAO.WarehousesDAO`
- Operaciones principales: `registrarAlmacen`, `modificarAlmacen`, `eliminarLogico`, `restaurarAlmacen`, `eliminarDefinitivo`, `listarActivos`, `listarInactivos`, `buscarAlmacenes`

### `Inventory`
- Modelo: `Models.Inventory`
- Controlador: `Controllers.InventarioController`
- Persistencia: `Repositories.IInventoryRepository`, `Repositories.InventoryRepository`, `DAO.InventoryDAO`
- Operaciones principales: `registrarInventario`, `modificarInventario`, `listarActivos`, `buscarInventario`, `transferirExistencias`, `listarAlmacenes`, `listarProductos`

### `Clients`
- Modelo: `Models.Client`
- Controlador: `Controllers.ClientController`
- Persistencia: `Repositories.IClientRepository`, `Repositories.ClientRepository`, `DAO.ClientDAO`
- Operaciones principales: `createClient`, `updateClient`, `softDeleteClient`, `deleteClient`, `restoreClient`, `findClientById`, `searchActiveClients`, `searchInactiveClients`, `listDocumentTypes`, `listClientOptions`
- Clases internas: `Client.Filter`, `Client.ReferenceItem`, `Client.PaginatedResult`

### `Shipments`
- Modelo: `Models.Shipment`
- Controlador: `Controllers.ShipmentController`
- Persistencia: `Repositories.IShipmentRepository`, `Repositories.ShipmentRepository`, `DAO.ShipmentDAO`
- Operaciones principales: `createShipment`, `updateShipment`, `searchShipments`, `findShipmentById`, `findShipmentByTrackingCode`, `getShipmentDetail`, `changeShipmentStatus`, `markShipmentAsDelivered`, `cancelShipment`, `registerTracking`
- Clases internas: `Shipment.Filter`, `Shipment.ReferenceItem`, `Shipment.PaginatedResult`, `Shipment.Tracking`, `Shipment.Detail`

### `Boxes`
- Modelo: `Models.Box`
- Controlador: `Controllers.BoxController`
- Persistencia: `Repositories.IBoxRepository`, `DAO.BoxDAO`
- Operaciones principales: `createBox`, `updateBox`, `deleteBox`, `findBoxById`, `listBoxes`, `listBoxesByShipment`, `listShipmentOptions`

### `ShipmentDetails`
- Modelo: `Models.ShipmentDetail`
- Controlador: `Controllers.ShipmentDetailController`
- Persistencia: `Repositories.IShipmentDetailRepository`, `DAO.ShipmentDetailDAO`
- Operaciones principales: `createDetail`, `updateDetail`, `deleteDetail`, `listDetails`, `listDetailsByShipment`, `listDetailsByBox`, `listShipmentOptions`, `listBoxOptions`, `listBoxOptionsByShipment`, `listProductOptions`

### `ShipmentTracking`
- Modelo: `Models.ShipmentTracking`
- Controlador: `Controllers.ShipmentTrackingController`
- Persistencia: `Repositories.IShipmentTrackingRepository`, `DAO.ShipmentTrackingDAO`
- Operaciones principales: `createTracking`, `updateTracking`, `deleteTracking`, `listTracking`, `listTrackingByShipment`, `listShipmentOptions`, `listUserOptions`

## Observaciones utiles

1. `DocumentTypes` no tiene clase, DAO o controlador propio; se trata como dato de referencia para clientes.
2. `Inventory` agrega propiedades de UI (`warehouseName`, `productName`, `productSku`) que no pertenecen directamente a la tabla.
3. `ShipmentTracking` existe como clase independiente y tambien como clase interna `Shipment.Tracking`.
4. `BoxDAO`, `ShipmentDetailDAO` y `ShipmentTrackingDAO` implementan interfaces de repositorio directamente, sin una clase `Repository` intermedia.
5. `Shipments` y `Clients` son los modulos con mas clases auxiliares para filtros, paginacion y combos.
6. El SQL compartido incluye `Warehouses.image_path`, pero `Models.Warehouses`, `AlmacenesController` y su repositorio no exponen hoy un campo `imagePath`.
7. `Inventory` declara una propiedad `status` en el modelo, aunque en el SQL que compartiste la tabla `Inventory` no muestra esa columna.

## Archivo del diagrama

- SVG vectorial: `docs/diagrama-clases-logistica.svg`
