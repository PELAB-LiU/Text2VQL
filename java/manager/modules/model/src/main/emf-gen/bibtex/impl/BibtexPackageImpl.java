/**
 */
package bibtex.impl;

import bibtex.Article;
import bibtex.Author;
import bibtex.AuthoredEntry;
import bibtex.BibTeXEntry;
import bibtex.BibTeXFile;
import bibtex.BibtexFactory;
import bibtex.BibtexPackage;
import bibtex.Book;
import bibtex.BookTitledEntry;
import bibtex.Booklet;
import bibtex.DatedEntry;
import bibtex.InBook;
import bibtex.InCollection;
import bibtex.InProceedings;
import bibtex.Manual;
import bibtex.MasterThesis;
import bibtex.Misc;
import bibtex.PhDThesis;
import bibtex.Proceedings;
import bibtex.TechReport;
import bibtex.ThesisEntry;
import bibtex.TitledEntry;
import bibtex.Unpublished;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import primitivetypes.PrimitivetypesPackage;

import primitivetypes.impl.PrimitivetypesPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class BibtexPackageImpl extends EPackageImpl implements BibtexPackage
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass bibTeXFileEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass authorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass bibTeXEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass authoredEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass datedEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass titledEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass bookTitledEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass articleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass techReportEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass unpublishedEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass manualEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass proceedingsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass inProceedingsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass bookletEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass bookEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass inCollectionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass inBookEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass miscEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass thesisEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass phDThesisEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass masterThesisEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see bibtex.BibtexPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private BibtexPackageImpl()
	{
		super(eNS_URI, BibtexFactory.eINSTANCE);
	}
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link BibtexPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static BibtexPackage init()
	{
		if (isInited) return (BibtexPackage)EPackage.Registry.INSTANCE.getEPackage(BibtexPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredBibtexPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		BibtexPackageImpl theBibtexPackage = registeredBibtexPackage instanceof BibtexPackageImpl ? (BibtexPackageImpl)registeredBibtexPackage : new BibtexPackageImpl();

		isInited = true;

		// Obtain or create and register interdependencies
		Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(PrimitivetypesPackage.eNS_URI);
		PrimitivetypesPackageImpl thePrimitivetypesPackage = (PrimitivetypesPackageImpl)(registeredPackage instanceof PrimitivetypesPackageImpl ? registeredPackage : PrimitivetypesPackage.eINSTANCE);

		// Create package meta-data objects
		theBibtexPackage.createPackageContents();
		thePrimitivetypesPackage.createPackageContents();

		// Initialize created meta-data
		theBibtexPackage.initializePackageContents();
		thePrimitivetypesPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theBibtexPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(BibtexPackage.eNS_URI, theBibtexPackage);
		return theBibtexPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getBibTeXFile()
	{
		return bibTeXFileEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getBibTeXFile_Entries()
	{
		return (EReference)bibTeXFileEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAuthor()
	{
		return authorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAuthor_Author()
	{
		return (EAttribute)authorEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getBibTeXEntry()
	{
		return bibTeXEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getBibTeXEntry_Id()
	{
		return (EAttribute)bibTeXEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAuthoredEntry()
	{
		return authoredEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAuthoredEntry_Authors()
	{
		return (EReference)authoredEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDatedEntry()
	{
		return datedEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDatedEntry_Year()
	{
		return (EAttribute)datedEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getTitledEntry()
	{
		return titledEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getTitledEntry_Title()
	{
		return (EAttribute)titledEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getBookTitledEntry()
	{
		return bookTitledEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getBookTitledEntry_Booktitle()
	{
		return (EAttribute)bookTitledEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getArticle()
	{
		return articleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getArticle_Journal()
	{
		return (EAttribute)articleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getTechReport()
	{
		return techReportEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getUnpublished()
	{
		return unpublishedEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getUnpublished_Note()
	{
		return (EAttribute)unpublishedEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getManual()
	{
		return manualEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getProceedings()
	{
		return proceedingsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getInProceedings()
	{
		return inProceedingsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getBooklet()
	{
		return bookletEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getBook()
	{
		return bookEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getBook_Publisher()
	{
		return (EAttribute)bookEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getInCollection()
	{
		return inCollectionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getInBook()
	{
		return inBookEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getInBook_Chapter()
	{
		return (EAttribute)inBookEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMisc()
	{
		return miscEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getThesisEntry()
	{
		return thesisEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getThesisEntry_School()
	{
		return (EAttribute)thesisEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getPhDThesis()
	{
		return phDThesisEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMasterThesis()
	{
		return masterThesisEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BibtexFactory getBibtexFactory()
	{
		return (BibtexFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents()
	{
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		bibTeXFileEClass = createEClass(BIB_TE_XFILE);
		createEReference(bibTeXFileEClass, BIB_TE_XFILE__ENTRIES);

		authorEClass = createEClass(AUTHOR);
		createEAttribute(authorEClass, AUTHOR__AUTHOR);

		bibTeXEntryEClass = createEClass(BIB_TE_XENTRY);
		createEAttribute(bibTeXEntryEClass, BIB_TE_XENTRY__ID);

		authoredEntryEClass = createEClass(AUTHORED_ENTRY);
		createEReference(authoredEntryEClass, AUTHORED_ENTRY__AUTHORS);

		datedEntryEClass = createEClass(DATED_ENTRY);
		createEAttribute(datedEntryEClass, DATED_ENTRY__YEAR);

		titledEntryEClass = createEClass(TITLED_ENTRY);
		createEAttribute(titledEntryEClass, TITLED_ENTRY__TITLE);

		bookTitledEntryEClass = createEClass(BOOK_TITLED_ENTRY);
		createEAttribute(bookTitledEntryEClass, BOOK_TITLED_ENTRY__BOOKTITLE);

		articleEClass = createEClass(ARTICLE);
		createEAttribute(articleEClass, ARTICLE__JOURNAL);

		techReportEClass = createEClass(TECH_REPORT);

		unpublishedEClass = createEClass(UNPUBLISHED);
		createEAttribute(unpublishedEClass, UNPUBLISHED__NOTE);

		manualEClass = createEClass(MANUAL);

		proceedingsEClass = createEClass(PROCEEDINGS);

		inProceedingsEClass = createEClass(IN_PROCEEDINGS);

		bookletEClass = createEClass(BOOKLET);

		bookEClass = createEClass(BOOK);
		createEAttribute(bookEClass, BOOK__PUBLISHER);

		inCollectionEClass = createEClass(IN_COLLECTION);

		inBookEClass = createEClass(IN_BOOK);
		createEAttribute(inBookEClass, IN_BOOK__CHAPTER);

		miscEClass = createEClass(MISC);

		thesisEntryEClass = createEClass(THESIS_ENTRY);
		createEAttribute(thesisEntryEClass, THESIS_ENTRY__SCHOOL);

		phDThesisEClass = createEClass(PH_DTHESIS);

		masterThesisEClass = createEClass(MASTER_THESIS);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents()
	{
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		PrimitivetypesPackage thePrimitivetypesPackage = (PrimitivetypesPackage)EPackage.Registry.INSTANCE.getEPackage(PrimitivetypesPackage.eNS_URI);

		// Add supertypes to classes
		authoredEntryEClass.getESuperTypes().add(this.getBibTeXEntry());
		datedEntryEClass.getESuperTypes().add(this.getBibTeXEntry());
		titledEntryEClass.getESuperTypes().add(this.getBibTeXEntry());
		bookTitledEntryEClass.getESuperTypes().add(this.getBibTeXEntry());
		articleEClass.getESuperTypes().add(this.getAuthoredEntry());
		articleEClass.getESuperTypes().add(this.getDatedEntry());
		articleEClass.getESuperTypes().add(this.getTitledEntry());
		techReportEClass.getESuperTypes().add(this.getAuthoredEntry());
		techReportEClass.getESuperTypes().add(this.getDatedEntry());
		techReportEClass.getESuperTypes().add(this.getTitledEntry());
		unpublishedEClass.getESuperTypes().add(this.getAuthoredEntry());
		unpublishedEClass.getESuperTypes().add(this.getTitledEntry());
		manualEClass.getESuperTypes().add(this.getTitledEntry());
		proceedingsEClass.getESuperTypes().add(this.getDatedEntry());
		proceedingsEClass.getESuperTypes().add(this.getTitledEntry());
		inProceedingsEClass.getESuperTypes().add(this.getProceedings());
		inProceedingsEClass.getESuperTypes().add(this.getAuthoredEntry());
		inProceedingsEClass.getESuperTypes().add(this.getBookTitledEntry());
		bookletEClass.getESuperTypes().add(this.getDatedEntry());
		bookEClass.getESuperTypes().add(this.getAuthoredEntry());
		bookEClass.getESuperTypes().add(this.getDatedEntry());
		bookEClass.getESuperTypes().add(this.getTitledEntry());
		inCollectionEClass.getESuperTypes().add(this.getBook());
		inCollectionEClass.getESuperTypes().add(this.getBookTitledEntry());
		inBookEClass.getESuperTypes().add(this.getBook());
		miscEClass.getESuperTypes().add(this.getBibTeXEntry());
		thesisEntryEClass.getESuperTypes().add(this.getAuthoredEntry());
		thesisEntryEClass.getESuperTypes().add(this.getDatedEntry());
		thesisEntryEClass.getESuperTypes().add(this.getTitledEntry());
		phDThesisEClass.getESuperTypes().add(this.getThesisEntry());
		masterThesisEClass.getESuperTypes().add(this.getThesisEntry());

		// Initialize classes and features; add operations and parameters
		initEClass(bibTeXFileEClass, BibTeXFile.class, "BibTeXFile", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getBibTeXFile_Entries(), this.getBibTeXEntry(), null, "entries", null, 0, -1, BibTeXFile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(authorEClass, Author.class, "Author", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAuthor_Author(), thePrimitivetypesPackage.getString(), "author", null, 1, 1, Author.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(bibTeXEntryEClass, BibTeXEntry.class, "BibTeXEntry", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getBibTeXEntry_Id(), thePrimitivetypesPackage.getString(), "id", null, 1, 1, BibTeXEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(authoredEntryEClass, AuthoredEntry.class, "AuthoredEntry", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getAuthoredEntry_Authors(), this.getAuthor(), null, "authors", null, 1, -1, AuthoredEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(datedEntryEClass, DatedEntry.class, "DatedEntry", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDatedEntry_Year(), thePrimitivetypesPackage.getString(), "year", null, 1, 1, DatedEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(titledEntryEClass, TitledEntry.class, "TitledEntry", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTitledEntry_Title(), thePrimitivetypesPackage.getString(), "title", null, 1, 1, TitledEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(bookTitledEntryEClass, BookTitledEntry.class, "BookTitledEntry", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getBookTitledEntry_Booktitle(), thePrimitivetypesPackage.getString(), "booktitle", null, 1, 1, BookTitledEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(articleEClass, Article.class, "Article", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getArticle_Journal(), thePrimitivetypesPackage.getString(), "journal", null, 1, 1, Article.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(techReportEClass, TechReport.class, "TechReport", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(unpublishedEClass, Unpublished.class, "Unpublished", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getUnpublished_Note(), thePrimitivetypesPackage.getString(), "note", null, 1, 1, Unpublished.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(manualEClass, Manual.class, "Manual", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(proceedingsEClass, Proceedings.class, "Proceedings", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(inProceedingsEClass, InProceedings.class, "InProceedings", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(bookletEClass, Booklet.class, "Booklet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(bookEClass, Book.class, "Book", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getBook_Publisher(), thePrimitivetypesPackage.getString(), "publisher", null, 1, 1, Book.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(inCollectionEClass, InCollection.class, "InCollection", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(inBookEClass, InBook.class, "InBook", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getInBook_Chapter(), thePrimitivetypesPackage.getInteger(), "chapter", null, 1, 1, InBook.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(miscEClass, Misc.class, "Misc", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(thesisEntryEClass, ThesisEntry.class, "ThesisEntry", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getThesisEntry_School(), thePrimitivetypesPackage.getString(), "school", null, 1, 1, ThesisEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(phDThesisEClass, PhDThesis.class, "PhDThesis", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(masterThesisEClass, MasterThesis.class, "MasterThesis", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //BibtexPackageImpl
