import { separarAtajo } from '../utils/enlaces.js';

/** Enlace acortado funcional (redirecciona) con el código resaltado. */
export default function EnlaceCorto({ url, grande = false }) {
  const { base, codigo } = separarAtajo(url);
  return (
    <a
      href={url}
      target="_blank"
      rel="noopener noreferrer"
      className={grande ? 'atajo atajo-grande' : 'atajo'}
      title={`Abrir ${url}`}
    >
      <span className="atajo-base">{base}</span>
      <span className="atajo-codigo">{codigo}</span>
    </a>
  );
}
