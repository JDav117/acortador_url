const RADIO = 9;
const CIRCUNFERENCIA = 2 * Math.PI * RADIO;

/**
 * 1.ª consulta (fuente MongoDB): solo informa que el resultado quedó en caché.
 * 2.ª consulta en adelante (fuente Redis): muestra los segundos que le quedan a la caché, decreciendo.
 */
export default function IndicadorCache({ datos, restanteMs }) {
  const totalSegundos = Math.round(datos.ttlCacheMs / 1000);

  if (!datos.desdeCache) {
    return (
      <div className="indicador indicador-mongo">
        <span className="indicador-punto" />
        <span>
          Leído de <strong>MongoDB</strong>
          <small>en caché por {totalSegundos} s</small>
        </span>
      </div>
    );
  }

  if (restanteMs === 0) {
    return (
      <div className="indicador indicador-expirado">
        <span className="indicador-punto" />
        <span>
          Caché <strong>Redis</strong> expirada
          <small>la próxima consulta irá a MongoDB</small>
        </span>
      </div>
    );
  }

  const segundos = Math.ceil(restanteMs / 1000);
  const progreso = Math.min(1, restanteMs / datos.ttlCacheMs);

  return (
    <div className="indicador indicador-redis" role="timer" aria-label={`Caché Redis: ${segundos} segundos restantes`}>
      <svg className="anillo" viewBox="0 0 24 24" aria-hidden="true">
        <circle cx="12" cy="12" r={RADIO} className="anillo-pista" />
        <circle
          cx="12"
          cy="12"
          r={RADIO}
          className="anillo-progreso"
          strokeDasharray={CIRCUNFERENCIA}
          strokeDashoffset={CIRCUNFERENCIA * (1 - progreso)}
        />
      </svg>
      <span>
        Desde caché <strong>Redis</strong>
        <small>
          <b>{segundos}</b> {segundos === 1 ? 'segundo' : 'segundos'} restantes
        </small>
      </span>
    </div>
  );
}
