from enum import Enum

from pydantic import BaseModel


class BedStatus(str, Enum):
    ready = "ready"
    preparing = "preparing"
    occupied = "occupied"
    cleaning = "cleaning"


class ProcedureCategory(str, Enum):
    surgery = "surgery"
    diagnostic = "diagnostic"
    treatment = "treatment"
    recovery = "recovery"
    other = "other"


class ProcedureTypeSummary(BaseModel):
    id: str
    name: str
    typical_duration_minutes: int


class ChangeReason(str, Enum):
    procedure_assignment = "procedure_assignment"
    doctor_adjustment = "doctor_adjustment"
    emergency_extension = "emergency_extension"
    manual_change = "manual_change"
    late_procedure = "late_procedure"


class Bed(BaseModel):
    id: str
    bed_number: str
    ward_id: str
    status: BedStatus
    status_changed_at: str
    expected_duration_minutes: int | None
    occupied_minutes: int | None = None
    is_overdue: bool = False
    overdue_minutes: int = 0
    remaining_minutes: int | None = None
    current_procedure_type_id: str | None = None
    procedure_type: ProcedureTypeSummary | None = None
    procedure_notes: str | None = None
    duration_changed_at: str | None = None


class BedStatusUpdate(BaseModel):
    status: BedStatus
    # Only meaningful when transitioning to 'occupied' — see
    # routers/beds.py's shared _occupy_bed helper.
    procedure_type_id: str | None = None
    expected_duration_minutes: int | None = None
    procedure_notes: str | None = None


class BedProcedureAssign(BaseModel):
    procedure_type_id: str
    expected_duration_minutes: int | None = None
    procedure_notes: str | None = None


class BedDurationUpdate(BaseModel):
    expected_duration_minutes: int
    change_reason: ChangeReason = ChangeReason.manual_change
    notes: str | None = None


class ProcedureType(BaseModel):
    id: str
    name: str
    description: str | None = None
    category: ProcedureCategory
    typical_duration_minutes: int
    min_duration_minutes: int | None = None
    max_duration_minutes: int | None = None
    is_active: bool = True
    created_at: str
    updated_at: str


class ProcedureTypeCreate(BaseModel):
    name: str
    description: str | None = None
    category: ProcedureCategory
    typical_duration_minutes: int
    min_duration_minutes: int | None = None
    max_duration_minutes: int | None = None


class ProcedureTypeUpdate(BaseModel):
    name: str | None = None
    description: str | None = None
    category: ProcedureCategory | None = None
    typical_duration_minutes: int | None = None
    min_duration_minutes: int | None = None
    max_duration_minutes: int | None = None
    is_active: bool | None = None


class Specialization(str, Enum):
    surgery = "Surgery"
    cardiology = "Cardiology"
    trauma = "Trauma"
    emergency_medicine = "Emergency Medicine"
    internal_medicine = "Internal Medicine"
    neurology = "Neurology"
    oncology = "Oncology"
    pediatrics = "Pediatrics"
    psychiatry = "Psychiatry"
    orthopedics = "Orthopedics"
    other = "Other"


class QualificationType(str, Enum):
    board_certification = "board_certification"
    degree = "degree"
    fellowship = "fellowship"
    certification = "certification"
    other = "other"


class Qualification(BaseModel):
    type: QualificationType
    name: str


class Doctor(BaseModel):
    id: str
    first_name: str
    last_name: str
    email: str
    phone: str
    license_number: str
    specialization: Specialization
    secondary_specializations: list[Specialization] = []
    ward_id: str | None = None
    qualifications: list[Qualification] = []
    bio: str | None = None
    years_of_experience: int | None = None
    is_active: bool = True
    created_at: str
    updated_at: str


class DoctorCreate(BaseModel):
    first_name: str
    last_name: str
    email: str
    phone: str
    license_number: str
    specialization: Specialization
    secondary_specializations: list[Specialization] = []
    ward_id: str | None = None
    qualifications: list[Qualification] = []
    bio: str | None = None
    years_of_experience: int | None = None


class DoctorUpdate(BaseModel):
    first_name: str | None = None
    last_name: str | None = None
    email: str | None = None
    phone: str | None = None
    license_number: str | None = None
    specialization: Specialization | None = None
    secondary_specializations: list[Specialization] | None = None
    ward_id: str | None = None
    qualifications: list[Qualification] | None = None
    bio: str | None = None
    years_of_experience: int | None = None
    is_active: bool | None = None


class Role(str, Enum):
    user = "user"
    admin = "admin"


class Language(str, Enum):
    el_gr = "el-GR"
    en_gb = "en-GB"


class AdminProfile(BaseModel):
    can_manage_wards: bool
    can_manage_beds: bool
    can_manage_users: bool
    permissions: list[str]


class User(BaseModel):
    id: str
    name: str
    email: str
    phone: str | None = None
    job_title: str | None = None
    role: Role
    ward_id: str | None = None
    # Nullable — null means "use the organization's default_language" (see
    # docs/features/internationalization-i18n.md's resolution order).
    language: Language | None = None
    admin_profile: AdminProfile | None = None


class UserUpdate(BaseModel):
    name: str | None = None
    phone: str | None = None
    job_title: str | None = None
    language: Language | None = None


class UserRoleUpdate(BaseModel):
    role: Role


class Ward(BaseModel):
    id: str
    name: str
    description: str | None = None
    bed_count: int = 0
    available_beds: int = 0
    created_at: str
    updated_at: str


class WardCreate(BaseModel):
    name: str
    description: str | None = None


class WardUpdate(BaseModel):
    name: str | None = None
    description: str | None = None


class InsightsOverview(BaseModel):
    total_beds: int
    occupied_beds: int
    preparing_beds: int
    ready_beds: int
    cleaning_beds: int
    availability_rate: float
    occupancy_rate: float
    avg_occupancy_duration: float | None = None
    avg_duration_variance: float | None = None
    overdue_beds: int


class WardDurationAnalysis(BaseModel):
    ward_id: str
    ward_name: str
    total_occupancies: int
    on_track: int
    overrun: int
    underutilized: int
    avg_variance_percent: float
    total_overrun_minutes: int
    total_underutil_minutes: int


class VarianceBucket(BaseModel):
    count: int
    percent: float


class VarianceDistribution(BaseModel):
    very_underutilized: VarianceBucket
    underutilized: VarianceBucket
    on_track: VarianceBucket
    overrun: VarianceBucket
    very_overrun: VarianceBucket


class Organization(BaseModel):
    id: str
    name: str
    default_language: Language
    timezone: str
