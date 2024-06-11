export interface UserDTO {
  id?: number;
  nickname?: string;
  email?: string;
  roles?: string[];
  submissions: number;
  contestsParticipated: number;
  acceptedSubmissions: number;

}