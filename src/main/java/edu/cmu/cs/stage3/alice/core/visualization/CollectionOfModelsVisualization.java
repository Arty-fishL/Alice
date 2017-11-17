package edu.cmu.cs.stage3.alice.core.visualization;

import edu.cmu.cs.stage3.alice.core.Collection;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.TextureMap;
import edu.cmu.cs.stage3.alice.core.Variable;

public abstract class CollectionOfModelsVisualization extends edu.cmu.cs.stage3.alice.core.Visualization {
	private final java.util.Vector m_bins = new java.util.Vector();

	@Override
	public void unhook(final Model model) {
		final int i = indexOf(model, 0);
		if (i != -1) {
			set(i, null);
		}
	}

	protected String getItemsName() {
		return "items";
	}

	private Variable m_itemsVariable = null;

	private Variable getItemsVariable() {
		if (m_itemsVariable == null) {
			m_itemsVariable = (Variable) getChildNamed(getItemsName());
		}
		return m_itemsVariable;
	}

	public Collection getItemsCollection() {
		return (Collection) getItemsVariable().value.getValue();
	}

	public Model[] getItems() {
		return (Model[]) getItemsCollection().values.getArrayValue();
	}

	public void setItems(final Model[] items) {
		getItemsCollection().values.set(items);
	}

	private Model getPrototype() {
		return (Model) getChildNamed("BinPrototype");
	}

	private int getBinCount() {
		return m_bins.size();
	}

	private Model getBinAt(final int i) {
		return (Model) m_bins.get(i);
	}

	private void setBinAt(final int i, final Model bin) {
		if (m_bins.size() == i) {
			m_bins.addElement(bin);
		} else {
			if (m_bins.size() < i) {
				m_bins.ensureCapacity(i + 1);
			}
			m_bins.set(i, bin);
		}
	}

	private static final java.awt.Font s_font = new java.awt.Font("Serif", java.awt.Font.PLAIN, 32);

	private static TextureMap getEmptyTextureMap(final Model bin) {
		return (TextureMap) bin.getChildNamed("EmptyTexture");
	}

	private static TextureMap getFilledTextureMap(final Model bin) {
		return (TextureMap) bin.getChildNamed("FilledTexture");
	}

	private static void decorateTextureMap(final TextureMap skin, final int i) {
		if (skin != null) {
			final java.awt.Image originalImage = skin.image.getImageValue();
			if (originalImage instanceof java.awt.image.BufferedImage) {
				final java.awt.image.BufferedImage originalBufferedImage = (java.awt.image.BufferedImage) originalImage;
				final java.awt.Image image = new java.awt.image.BufferedImage(originalBufferedImage.getWidth(),
						originalBufferedImage.getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB);
				final java.awt.Graphics g = image.getGraphics();
				g.drawImage(originalImage, 0, 0, null);
				g.setFont(s_font);
				final String s = Integer.toString(i);
				final java.awt.FontMetrics fm = g.getFontMetrics();
				final java.awt.geom.Rectangle2D r = fm.getStringBounds(s, g);
				g.setColor(java.awt.Color.black);
				g.drawString(s, 80, (int) (20 - r.getX() + r.getHeight()));
				g.dispose();
				skin.image.set(image);
				skin.touchImage();
			}
		}
	}

	private void synchronize(final Model[] curr) {
		int binCount = getBinCount();
		for (int i = binCount - 1; i >= curr.length; i--) {
			final Model binI = getBinAt(i);
			binI.vehicle.set(null);
			// binI.removeFromParent();
			m_bins.remove(binI);
		}
		final Model prototype = getPrototype();
		if (prototype != null) {
			for (int i = binCount; i < curr.length; i++) {
				final Class[] share = { edu.cmu.cs.stage3.alice.core.Geometry.class };
				final String name = "Sub" + i;
				Model binI = (Model) getChildNamed(name);
				if (binI == null) {
					binI = (Model) prototype.HACK_createCopy(name, this, -1, share, null);
					decorateTextureMap(getEmptyTextureMap(binI), i);
					decorateTextureMap(getFilledTextureMap(binI), i);
				}
				setBinAt(i, binI);
			}
			binCount = getBinCount();
			for (int i = 0; i < binCount; i++) {
				final Model binI = getBinAt(i);
				binI.vehicle.set(this);
				binI.setPositionRightNow(-(prototype.getWidth() * i), 0, 0);
				if (curr[i] != null) {
					curr[i].vehicle.set(binI);
					curr[i].visualization.set(this);
					curr[i].setTransformationRightNow(getTransformationFor(curr[i], i), this);
					binI.diffuseColorMap.set(getFilledTextureMap(binI));
				} else {
					binI.diffuseColorMap.set(getEmptyTextureMap(binI));
				}
			}
			final Model rightBracket = (Model) getChildNamed("RightBracket");
			if (rightBracket != null) {
				rightBracket.setPositionRightNow(-(prototype.getWidth() * (binCount - 0.5)), 0, 0);
			}
		}
	}

	public Model get(final int i) {
		return (Model) getItemsCollection().values.get(i);
	}

	public void set(final int i, final Model model) {
		getItemsCollection().values.set(i, model);
	}

	public int indexOf(final Model model, final int from) {
		return getItemsCollection().values.indexOf(model, from);
	}

	public int lastIndexOf(final Model model, final int from) {
		return getItemsCollection().values.lastIndexOf(model, from);
	}

	public boolean contains(final Model model) {
		return getItemsCollection().values.contains(model);
	}

	public int size() {
		return getItemsCollection().values.size();
	}

	public boolean isEmpty() {
		return getItemsCollection().values.isEmpty();
	}

	@Override
	protected void loadCompleted() {
		super.loadCompleted();
		final Collection collection = getItemsCollection();
		if (collection != null) {
			collection.values.addPropertyListener(new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
				@Override
				public void propertyChanging(final edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {
				}

				@Override
				public void propertyChanged(final edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {
					CollectionOfModelsVisualization.this.synchronize((Model[]) propertyEvent.getValue());
				}
			});
			synchronize(getItems());
		} else {
			System.err.println("WARNING: collection is null " + this);
		}
	}

	public javax.vecmath.Matrix4d getTransformationFor(final edu.cmu.cs.stage3.alice.core.Model model, final int i) {
		final Model prototype = getPrototype();

		final javax.vecmath.Matrix4d m = new javax.vecmath.Matrix4d();
		m.setIdentity();
		if (model != null) {
			final edu.cmu.cs.stage3.math.Box box = model.getBoundingBox();
			final javax.vecmath.Vector3d v = box.getCenterOfBottomFace();
			if (v != null) {
				v.negate();
				m.m30 = v.x;
				m.m31 = v.y;
				m.m32 = v.z;
			}
		}
		m.m30 -= prototype.getWidth() * i;
		return m;
	}
}
