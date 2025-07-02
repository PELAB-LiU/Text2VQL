/**
 */
package bibtex;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Unpublished</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link bibtex.Unpublished#getNote <em>Note</em>}</li>
 * </ul>
 *
 * @see bibtex.BibtexPackage#getUnpublished()
 * @model
 * @generated
 */
public interface Unpublished extends AuthoredEntry, TitledEntry
{
	/**
	 * Returns the value of the '<em><b>Note</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Note</em>' attribute.
	 * @see #setNote(Object)
	 * @see bibtex.BibtexPackage#getUnpublished_Note()
	 * @model unique="false" dataType="primitivetypes.String" required="true" ordered="false"
	 * @generated
	 */
	Object getNote();

	/**
	 * Sets the value of the '{@link bibtex.Unpublished#getNote <em>Note</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Note</em>' attribute.
	 * @see #getNote()
	 * @generated
	 */
	void setNote(Object value);

} // Unpublished
