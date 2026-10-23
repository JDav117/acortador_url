import { useEffect, useRef, useState } from 'react';

const DURACION_COPIADO_MS = 1600;
const ETIQUETAS = { inicial: 'Copiar', copiado: 'Copiado', error: 'Error' };

/** Copia al portapapeles; el ícono de copiar se transforma en un check. */
export default function BotonCopiar({ texto, compacto = false }) {
  const [estado, setEstado] = useState('inicial'); // inicial | copiado | error
  const temporizador = useRef();

  useEffect(() => () => clearTimeout(temporizador.current), []);

  async function copiar() {
    try {
      await copiarAlPortapapeles(texto);
      setEstado('copiado');
    } catch {
      setEstado('error');
    }
    clearTimeout(temporizador.current);
    temporizador.current = setTimeout(() => setEstado('inicial'), DURACION_COPIADO_MS);
  }

  const etiqueta = ETIQUETAS[estado];
  const clases = ['btn', compacto ? 'btn-icono' : 'btn-secundario', 'btn-copiar', `es-${estado}`].join(' ');

  return (
    <button type="button" className={clases} onClick={copiar} title={etiqueta} aria-label={etiqueta}>
      <span className="iconos" aria-hidden="true">
        <svg className="icono-copiar" viewBox="0 0 24 24">
          <rect x="9" y="9" width="11" height="11" rx="2.5" />
          <path d="M5 15V6a1 1 0 0 1 1-1h9" />
        </svg>
        <svg className="icono-check" viewBox="0 0 24 24">
          <path d="M5 12.5l4.5 4.5L19 7.5" />
        </svg>
      </span>
      {!compacto && <span className="btn-copiar-texto">{etiqueta}</span>}
    </button>
  );
}

async function copiarAlPortapapeles(texto) {
  if (navigator.clipboard?.writeText) {
    await navigator.clipboard.writeText(texto);
    return;
  }
  // Respaldo para contextos sin la API de portapapeles (p. ej. http en otra IP).
  const area = document.createElement('textarea');
  area.value = texto;
  area.setAttribute('readonly', '');
  area.style.position = 'fixed';
  area.style.opacity = '0';
  document.body.appendChild(area);
  area.select();
  const ok = document.execCommand('copy');
  document.body.removeChild(area);
  if (!ok) throw new Error('No se pudo copiar');
}
