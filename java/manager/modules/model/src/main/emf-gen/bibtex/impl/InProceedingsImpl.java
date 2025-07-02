/**
 */
package bibtex.impl;

import bibtex.Author;
import bibtex.AuthoredEntry;
import bibtex.BibtexPackage;
import bibtex.BookTitledEntry;
import bibtex.InProceedings;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>In Proceedings</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link bibtex.impl.InProceedingsImpl#getAuthors <em>Authors</em>}</li>
 *   <li>{@link bibtex.impl.InProceedingsImpl#getBooktitle <em>Booktitle</em>}</li>
 * </ul>
 *
 * @generated
 */
public class InProceedingsImpl extends ProceedingsImpl implements InProceedings
{
	/**
	 * The cached value of the '{@link #getAuthors() <em>Authors</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAuthors()
	 * @generated
	 * @ordered
	 */
	protected EList authors;

	/**
	 * The default value of the '{@link #getBooktitle() <em>Booktitle</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBooktitle()
	 * @generated
	 * @ordered
	 */
	protected static final Object BOOKTITLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBooktitle() <em>Booktitle</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBooktitle()
	 * @generated
	 * @ordered
	 */
	protected Object booktitle = BOOKTITLE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected InProceedingsImpl()
	{
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass()
	{
		return BibtexPackage.Literals.IN_PROCEEDINGS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getAuthors()
	{
		if (authors == null)
		{
			authors = new EObjectContainmentEList(Author.class, this, BibtexPackage.IN_PROCEEDINGS__AUTHORS);
		}
		return authors;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getBooktitle()
	{
		return booktitle;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBooktitle(Object newBooktitle)
	{
		Object oldBooktitle = booktitle;
		booktitle = newBooktitle;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BibtexPackage.IN_PROCEEDINGS__BOOKTITLE, oldBooktitle, booktitle));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
	{
		switch (featureID)
		{
			case BibtexPackage.IN_PROCEEDINGS__AUTHORS:
				return ((InternalEList)getAuthors()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType)
	{
		switch (featureID)
		{
			case BibtexPackage.IN_PROCEEDINGS__AUTHORS:
				return getAuthors();
			case BibtexPackage.IN_PROCEEDINGS__BOOKTITLE:
				return getBooktitle();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet(int featureID, Object newValue)
	{
		switch (featureID)
		{
			case BibtexPackage.IN_PROCEEDINGS__AUTHORS:
				getAuthors().clear();
				getAuthors().addAll((Collection)newValue);
				return;
			case BibtexPackage.IN_PROCEEDINGS__BOOKTITLE:
				setBooktitle((Object)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eUnset(int featureID)
	{
		switch (featureID)
		{
			case BibtexPackage.IN_PROCEEDINGS__AUTHORS:
				getAuthors().clear();
				return;
			case BibtexPackage.IN_PROCEEDINGS__BOOKTITLE:
				setBooktitle(BOOKTITLE_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean eIsSet(int featureID)
	{
		switch (featureID)
		{
			case BibtexPackage.IN_PROCEEDINGS__AUTHORS:
				return authors != null && !authors.isEmpty();
			case BibtexPackage.IN_PROCEEDINGS__BOOKTITLE:
				return BOOKTITLE_EDEFAULT == null ? booktitle != null : !BOOKTITLE_EDEFAULT.equals(booktitle);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class baseClass)
	{
		if (baseClass == AuthoredEntry.class)
		{
			switch (derivedFeatureID)
			{
				case BibtexPackage.IN_PROCEEDINGS__AUTHORS: return BibtexPackage.AUTHORED_ENTRY__AUTHORS;
				default: return -1;
			}
		}
		if (baseClass == BookTitledEntry.class)
		{
			switch (derivedFeatureID)
			{
				case BibtexPackage.IN_PROCEEDINGS__BOOKTITLE: return BibtexPackage.BOOK_TITLED_ENTRY__BOOKTITLE;
				default: return -1;
			}
		}
		return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class baseClass)
	{
		if (baseClass == AuthoredEntry.class)
		{
			switch (baseFeatureID)
			{
				case BibtexPackage.AUTHORED_ENTRY__AUTHORS: return BibtexPackage.IN_PROCEEDINGS__AUTHORS;
				default: return -1;
			}
		}
		if (baseClass == BookTitledEntry.class)
		{
			switch (baseFeatureID)
			{
				case BibtexPackage.BOOK_TITLED_ENTRY__BOOKTITLE: return BibtexPackage.IN_PROCEEDINGS__BOOKTITLE;
				default: return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String toString()
	{
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (booktitle: ");
		result.append(booktitle);
		result.append(')');
		return result.toString();
	}

} //InProceedingsImpl
