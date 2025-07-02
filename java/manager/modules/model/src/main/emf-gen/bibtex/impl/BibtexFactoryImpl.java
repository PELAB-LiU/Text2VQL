/**
 */
package bibtex.impl;

import bibtex.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class BibtexFactoryImpl extends EFactoryImpl implements BibtexFactory
{
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static BibtexFactory init()
	{
		try
		{
			BibtexFactory theBibtexFactory = (BibtexFactory)EPackage.Registry.INSTANCE.getEFactory(BibtexPackage.eNS_URI);
			if (theBibtexFactory != null)
			{
				return theBibtexFactory;
			}
		}
		catch (Exception exception)
		{
			EcorePlugin.INSTANCE.log(exception);
		}
		return new BibtexFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BibtexFactoryImpl()
	{
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EObject create(EClass eClass)
	{
		switch (eClass.getClassifierID())
		{
			case BibtexPackage.BIB_TE_XFILE: return createBibTeXFile();
			case BibtexPackage.AUTHOR: return createAuthor();
			case BibtexPackage.ARTICLE: return createArticle();
			case BibtexPackage.TECH_REPORT: return createTechReport();
			case BibtexPackage.UNPUBLISHED: return createUnpublished();
			case BibtexPackage.MANUAL: return createManual();
			case BibtexPackage.PROCEEDINGS: return createProceedings();
			case BibtexPackage.IN_PROCEEDINGS: return createInProceedings();
			case BibtexPackage.BOOKLET: return createBooklet();
			case BibtexPackage.BOOK: return createBook();
			case BibtexPackage.IN_COLLECTION: return createInCollection();
			case BibtexPackage.IN_BOOK: return createInBook();
			case BibtexPackage.MISC: return createMisc();
			case BibtexPackage.PH_DTHESIS: return createPhDThesis();
			case BibtexPackage.MASTER_THESIS: return createMasterThesis();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BibTeXFile createBibTeXFile()
	{
		BibTeXFileImpl bibTeXFile = new BibTeXFileImpl();
		return bibTeXFile;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Author createAuthor()
	{
		AuthorImpl author = new AuthorImpl();
		return author;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Article createArticle()
	{
		ArticleImpl article = new ArticleImpl();
		return article;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TechReport createTechReport()
	{
		TechReportImpl techReport = new TechReportImpl();
		return techReport;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Unpublished createUnpublished()
	{
		UnpublishedImpl unpublished = new UnpublishedImpl();
		return unpublished;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Manual createManual()
	{
		ManualImpl manual = new ManualImpl();
		return manual;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Proceedings createProceedings()
	{
		ProceedingsImpl proceedings = new ProceedingsImpl();
		return proceedings;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InProceedings createInProceedings()
	{
		InProceedingsImpl inProceedings = new InProceedingsImpl();
		return inProceedings;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Booklet createBooklet()
	{
		BookletImpl booklet = new BookletImpl();
		return booklet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Book createBook()
	{
		BookImpl book = new BookImpl();
		return book;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InCollection createInCollection()
	{
		InCollectionImpl inCollection = new InCollectionImpl();
		return inCollection;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InBook createInBook()
	{
		InBookImpl inBook = new InBookImpl();
		return inBook;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Misc createMisc()
	{
		MiscImpl misc = new MiscImpl();
		return misc;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PhDThesis createPhDThesis()
	{
		PhDThesisImpl phDThesis = new PhDThesisImpl();
		return phDThesis;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MasterThesis createMasterThesis()
	{
		MasterThesisImpl masterThesis = new MasterThesisImpl();
		return masterThesis;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BibtexPackage getBibtexPackage()
	{
		return (BibtexPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	public static BibtexPackage getPackage()
	{
		return BibtexPackage.eINSTANCE;
	}

} //BibtexFactoryImpl
