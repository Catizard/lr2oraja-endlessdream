package bms.player.beatoraja.play;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.FloatArray;

import bms.model.Mode;
import bms.player.beatoraja.ClearType;
import bms.player.beatoraja.CourseData;
import bms.player.beatoraja.PlayerResource;
import bms.player.beatoraja.play.GaugeProperty.GaugeElementProperty;
import bms.model.BMSModel;

/**
 * プレイゲージの抽象クラス
 * 
 * @author exch
 */
public final class GrooveGauge {

	public static final int ASSISTEASY = 0;
	public static final int EASY = 1;
	public static final int NORMAL = 2;
	public static final int HARD = 3;
	public static final int EXHARD = 4;
	public static final int HAZARD = 5;
	public static final int CLASS = 6;
	public static final int EXCLASS = 7;
	public static final int EXHARDCLASS = 8;

	private final int typeorg;
	private int type = -1;
	
	private Gauge[] gauges;

	public GrooveGauge(BMSModel model, int type, GaugeProperty property) {
		this.typeorg = this.type = type;
		this.gauges = new Gauge[property.values.length];
		for(int i = 0; i < property.values.length; i++) {
			this.gauges[i] = new Gauge(model, property.values[i], ClearType.getClearTypeByGauge(i));
		}
	}

	/**
	 * 判定に応じてゲージを更新する
	 * 
	 * @param judge
	 */
	public void update(int judge) {
		this.update(judge, 1);
	}

	/**
	 * 判定に応じてゲージを更新する
	 * 
	 * @param judge
	 */
	public void update(int judge, float rate) {
		for(Gauge gauge : gauges) {
			gauge.update(judge, rate);
		}
	}

	public void addValue(float value) {
		for(Gauge gauge : gauges) {
			gauge.setValue(gauge.getValue() + value);
		}
	}

	public float getValue() {
		return gauges[type].getValue();
	}

	public float getValue(int type) {
		return gauges[type].getValue();
	}

	public void setValue(float value) {
		for(Gauge gauge : gauges) {
			gauge.setValue(value);
		}
	}

	public void setValue(int type, float value) {
		gauges[type].setValue(value);
	}

	public boolean isQualified() {
		return gauges[type].isQualified();
	}

	public int getType() {
		return  type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public boolean isTypeChanged() {
		return typeorg != type;
	}

	public boolean isCourseGauge() {
		return type >= CLASS && type <= EXHARDCLASS;
	}
	
	public int getGaugeTypeLength() {
		return gauges.length;
	}

	public ClearType getClearType() {
		return gauges[type].cleartype;
	}

	public Gauge getGauge() {
		return gauges[type];
	}
	
	public Gauge getGauge(int type) {
		return gauges[type];
	}
	
	public static GrooveGauge create(BMSModel model, int type, PlayerResource resource) {
		int coursetype = 0;
		GaugeProperty gauges = null;
		if(resource.getCourseBMSModels() != null){
			coursetype = 1;
			for (CourseData.CourseDataConstraint i : resource.getConstraint()) {
				switch(i) {
				case GAUGE_5KEYS:
					gauges = GaugeProperty.FIVEKEYS;
					break;
				case GAUGE_7KEYS:
					gauges = GaugeProperty.SEVENKEYS;
					break;
				case GAUGE_9KEYS:
					gauges = GaugeProperty.PMS;
					break;
				case GAUGE_24KEYS:
					gauges = GaugeProperty.KEYBOARD;
					break;
				case GAUGE_LR2:
					gauges = GaugeProperty.LR2;
					break;
				default:
					break;
				}
			}
		}
		GrooveGauge gauge = create(model, type, coursetype, gauges);
		FloatArray[] f = resource.getGauge();
		if (f != null) {
			for(int i = 0; i < f.length; i++) {
				gauge.setValue(i, f[i].get(f[i].size - 1));
			}
		}
		return gauge;
	}
	
	public static GrooveGauge create(BMSModel model, int type, int grade, GaugeProperty gauge) {
		int id = -1;
		if (grade > 0) {
			// 段位ゲージ
			id = type <= 2 ? 6 : (type == 3 ? 7 : 8);
		} else {
			id = type;
		}
		if(id >= 0) {
			if(gauge == null) {
				Mode mode = model.getMode();
				gauge = BMSPlayerRule.getBMSPlayerRule(mode).gauge;
			}
			if(gauge != null) {
				return create(model, id, gauge);
			}
		}
		return null;
	}

	public static GrooveGauge create(BMSModel model, int id, GaugeProperty gauge) {
		return new GrooveGauge(model, id, gauge);
	}

	public static final class Gauge {
		/**
		 * ゲージの現在値
		 */
		private float value;
		/**
		 * ゲージの仕様
		 */
		private final GaugeElementProperty element;
		/**
		 * 各判定毎のゲージの増減
		 */
		private final float[] gauge;
		/**
		 * ゲージのクリアタイプ
		 */
		private final ClearType cleartype;

		public Gauge(BMSModel model, GaugeElementProperty element, ClearType cleartype) {
			this.element = element;
			this.value = element.init;
			this.cleartype = cleartype;
			this.gauge = element.value.clone();
			if(element.modifier != null) {
				for(int i = 0;i < gauge.length;i++) {
					gauge[i] = element.modifier.modify(gauge[i], model);
				}				
			}
		}
		
		public float getValue() {
			return value;
		}

		public void setValue(float value) {
			if(this.value > 0f) {
				this.value = MathUtils.clamp(value, element.min, element.max);				
				if (this.value < element.death) {
					this.value = 0;
				}
			}
		}
		
		/**
		 * 判定に応じてゲージを更新する
		 * 
		 * @param judge
		 */
		public void update(int judge, float rate) {
			float inc = gauge[judge] * rate;
			if(inc < 0) {
				for(float[] gut : element.guts) {
					if(value < gut[0]) {
						inc *= gut[1];
						break;
					}
				}
			}
			this.setValue(value + inc);
		}
		
		public GaugeElementProperty getProperty() {
			return element;
		}
		
		public boolean isQualified() {
			return value > 0f && value >= element.border;
		}
		
		public boolean isMax() {
			return value == element.max;
		}
	}
	
	public interface GaugeModifier {
		
		/**
		 * 回復量にTOTALを使用
		 */
		public static final GaugeModifier TOTAL = (f, model) -> (f > 0 ? (float) (f * model.getTotal() / model.getTotalNotes()) : f);

		/**
		 * TOTAL値によって回復量に制限をかける
		 */
		public static final GaugeModifier LIMIT_INCREMENT = (f, model) -> {
			final float pg = (float) Math.max(Math.min(0.15f, (2 * model.getTotal() - 320) / model.getTotalNotes()), 0);
			if(f > 0) {
				f *= pg / 0.15f;
			}
			return f;
		};
		
		/**
		 * TOTAL値、総ノート数によってダメージ量を増加させる
		 */
		public static final GaugeModifier MODIFY_DAMAGE = (f, model) -> {			
			if(f < 0) {
				float fix1=1.0f;
				float fix2=1.0f;

				// トータル補正 (<240)
				fix1 = (float)(10.0 / Math.min(10.0, Math.max(1.0, Math.floor(model.getTotal() / 16.0) - 5.0)));

				// ノート数補正 (<1000)
				if(model.getTotalNotes()<=20) {
					fix2 = 10.0f;
				}else if(model.getTotalNotes()<30) {
					fix2 = 8.0f + 0.2f*(30-model.getTotalNotes());
				}else if(model.getTotalNotes()<60) {
					fix2 = 5.0f + 0.2f*(60-model.getTotalNotes())/3.0f;
				}else if(model.getTotalNotes()<125) {
					fix2 = 4.0f + (125-model.getTotalNotes())/65.0f;
				}else if(model.getTotalNotes()<250) {
					fix2 = 3.0f + 0.008f*(250-model.getTotalNotes());
				}else if(model.getTotalNotes()<500) {
					fix2 = 2.0f + 0.004f*(500-model.getTotalNotes());
				}else if(model.getTotalNotes()<1000) {
					fix2 = 1.0f + 0.002f*(1000-model.getTotalNotes());
				}else {
					fix2 = 1.0f;
				}

				f *= Math.max(fix1, fix2);
			}
			return f;
		};
		
		public abstract float modify(float f, BMSModel model);		
	}
}
