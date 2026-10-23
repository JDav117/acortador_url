import AccionesEnlace from './AccionesEnlace.jsx';
import EnlaceCorto from './EnlaceCorto.jsx';
import IndicadorCache from './IndicadorCache.jsx';
import { separarEnlace } from '../utils/enlaces.js';
import { useCuentaRegresiva } from '../hooks/useCuentaRegresiva.js';

export default function VerEnlaces({ consulta, onConsultar, onIrAcortar }) {
  const { cargando, error, datos, expiraEn } = consulta;
  const restanteMs = useCuentaRegresiva(expiraEn);
  // La lista que vino de Redis ES la entrada de caché: cuando su TTL llega a 0 deja de existir
  // y se retira de la pantalla. La siguiente consulta la vuelve a leer desde MongoDB.
  const cacheExpirada = Boolean(datos?.desdeCache) && restanteMs === 0;
  const enlaces = cacheExpirada ? [] : (datos?.enlaces ?? []);

  return (
    <section className="historial">
      <div className="historial-cabecera">
        <div>
          <h1>Historial</h1>
          <p className="historial-sub">{subtitulo(datos, enlaces, cacheExpirada)}</p>
        </div>
        <div className="historial-herramientas">
          {datos && !cargando && <IndicadorCache datos={datos} restanteMs={restanteMs} />}
          <button
            type="button"
            className="btn btn-icono"
            onClick={onConsultar}
            disabled={cargando}
            title="Consultar de nuevo"
            aria-label="Consultar de nuevo"
          >
            <svg viewBox="0 0 24 24" className={cargando ? 'girando' : undefined} aria-hidden="true">
              <path d="M20 12a8 8 0 1 1-2.34-5.66M20 4v5h-5" />
            </svg>
          </button>
        </div>
      </div>

      {error && <p className="aviso aviso-error" role="alert">{error}</p>}

      {!datos && cargando && <p className="aviso">Consultando…</p>}

      {cacheExpirada && (
        <div className="vacio vacio-expirado">
          <p>
            <strong>La caché de Redis expiró.</strong>
            <br />
            Los enlaces que estaban en caché ya no existen. Consulta de nuevo para leer el historial desde MongoDB.
          </p>
          <button type="button" className="btn btn-primario" onClick={onConsultar} disabled={cargando}>
            {cargando ? 'Consultando…' : 'Consultar MongoDB'}
          </button>
        </div>
      )}

      {datos && !cacheExpirada && enlaces.length === 0 && (
        <div className="vacio">
          <p>Aún no hay enlaces guardados.</p>
          <button type="button" className="btn btn-primario" onClick={onIrAcortar}>
            Acortar el primero
          </button>
        </div>
      )}

      {enlaces.length > 0 && (
        <div className={cargando ? 'tabla-marco actualizando' : 'tabla-marco'}>
          <table className="tabla">
            <thead>
              <tr>
                <th className="col-num">#</th>
                <th className="col-id">ID</th>
                <th>Enlace original</th>
                <th>Enlace acortado</th>
                <th className="col-acciones"><span className="oculto">Acciones</span></th>
              </tr>
            </thead>
            <tbody>
              {enlaces.map((enlace, i) => {
                const { dominio, resto } = separarEnlace(enlace.enlaceOriginal);
                return (
                  <tr key={enlace.id}>
                    <td className="col-num">{String(i + 1).padStart(2, '0')}</td>
                    <td className="col-id" title={`ID en MongoDB: ${enlace.id}`}>
                      …{enlace.id.slice(-6)}
                    </td>
                    <td className="col-original" title={enlace.enlaceOriginal}>
                      <span className="dominio">{dominio}</span>
                      <span className="resto">{resto}</span>
                    </td>
                    <td className="col-atajo">
                      <EnlaceCorto url={enlace.enlaceAcortado} />
                    </td>
                    <td className="col-acciones">
                      <AccionesEnlace enlace={enlace.enlaceAcortado} compacto />
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}

function subtitulo(datos, enlaces, cacheExpirada) {
  if (!datos) return 'Enlaces guardados en MongoDB';
  if (cacheExpirada) return 'Sin datos en pantalla: la caché expiró';
  const cantidad = `${enlaces.length} ${enlaces.length === 1 ? 'enlace guardado' : 'enlaces guardados'}`;
  return datos.desdeCache ? `${cantidad} · copia en caché Redis` : `${cantidad} en MongoDB`;
}
