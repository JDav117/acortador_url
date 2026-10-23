import BotonCopiar from './BotonCopiar.jsx';

/** Botón de copiar + botón de redireccionamiento directo para un enlace acortado. */
export default function AccionesEnlace({ enlace, compacto = false }) {
  return (
    <div className="acciones">
      <BotonCopiar texto={enlace} compacto={compacto} />
      <a
        className={compacto ? 'btn btn-icono' : 'btn btn-secundario'}
        href={enlace}
        target="_blank"
        rel="noopener noreferrer"
        title="Ir al enlace"
        aria-label="Ir al enlace"
      >
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path d="M7 17L17 7M9 7h8v8" />
        </svg>
        {!compacto && <span>Ir</span>}
      </a>
    </div>
  );
}
