/**
 */
package bibtex;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Thesis Entry</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link bibtex.ThesisEntry#getSchool <em>School</em>}</li>
 * </ul>
 *
 * @see bibtex.BibtexPackage#getThesisEntry()
 * @model abstract="true"
 * @generated
 */
public interface ThesisEntry extends AuthoredEntry, DatedEntry, TitledEntry
{
	/**
	 * Returns the value of the '<em><b>School</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>School</em>' attribute.
	 * @see #setSchool(Object)
	 * @see bibtex.BibtexPackage#getThesisEntry_School()
	 * @model unique="false" dataType="primitivetypes.String" required="true" ordered="false"
	 * @generated
	 */
	Object getSchool();

	/**
	 * Sets the value of the '{@link bibtex.ThesisEntry#getSchool <em>School</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>School</em>' attribute.
	 * @see #getSchool()
	 * @generated
	 */
	void setSchool(Object value);

} // ThesisEntry
