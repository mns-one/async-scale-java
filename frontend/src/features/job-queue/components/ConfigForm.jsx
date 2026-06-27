import { styles } from "../styles";

export function ConfigForm({ clientId, size, count, interval_, target, onSizeChange, onCountChange,
    onIntervalChange, onTargetChange })
{
  return (
    <div style={styles.formGrid}>
      
      <div style={styles.field}>
        <label style={styles.label}>
          Job batch size <span style={{ opacity: 0.6 }}>(50-1000)</span>
        </label>
        <input style={styles.input} type="number" min={50} max={1000} step={1} value={size} onChange={onSizeChange} />
      </div>

      <div style={styles.field}>
        <label style={styles.label}>
          Number of Job batches <span style={{ opacity: 0.6 }}>(2-10)</span>
        </label>
        <input style={styles.input} type="number" min={2} max={10} step={1} value={count} onChange={onCountChange} />
      </div>

      <div style={styles.field}>
        <label style={styles.label}>
          Arrival Interval <span style={{ opacity: 0.6 }}>(2-10 seconds)</span>
        </label>
        <input style={styles.input} type="number" min={2} max={10} step={1} value={interval_} onChange={onIntervalChange} />
      </div>

      <div style={styles.field}>
        <label style={styles.label}>
          Processing Target % <span style={{ opacity: 0.6 }}>(10-80)</span>
        </label>
        <input style={styles.input} type="number" min={10} max={80} step={1} value={target} onChange={onTargetChange} />
      </div>

    </div>
  );
}

